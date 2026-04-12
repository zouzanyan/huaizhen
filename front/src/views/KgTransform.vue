<template>
  <div class="kg-transform">
    <div class="page-header">
      <span class="title">结构化数据导入</span>
      <el-select v-model="selectedProjectId" placeholder="请选择项目" @change="handleProjectChange" style="width: 300px;">
        <el-option
          v-for="project in projectList"
          :key="project.id"
          :label="project.name"
          :value="project.id"
        />
      </el-select>
    </div>

    <template v-if="selectedProjectId">
      <el-card style="margin-top: 20px;">
        <el-steps :active="currentStep" finish-status="success" style="margin-bottom: 30px;">
          <el-step title="上传文件" />
          <el-step title="配置映射" />
          <el-step title="确认导入" />
        </el-steps>

        <div v-show="currentStep === 0">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            accept=".xlsx,.xls,.csv"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 Excel (.xlsx, .xls) 和 CSV 文件
              </div>
            </template>
          </el-upload>
          <el-button type="primary" @click="handlePreview" :loading="previewLoading" style="margin-top: 20px;">
            下一步
          </el-button>
        </div>

        <div v-show="currentStep === 1">
          <el-form :model="transformForm" label-width="120px">
            <el-form-item label="实体类型" required>
              <el-select v-model="transformForm.entityType" placeholder="请选择实体类型">
                <el-option
                  v-for="et in entityTypes"
                  :key="et.id"
                  :label="et.name"
                  :value="et.name"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="名称列" required>
              <el-select v-model="transformForm.nameColumn" placeholder="请选择名称列">
                <el-option
                  v-for="header in headers"
                  :key="header"
                  :label="header"
                  :value="header"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="属性列">
              <el-select v-model="transformForm.propertyColumns" multiple placeholder="请选择属性列">
                <el-option
                  v-for="header in headers"
                  :key="header"
                  :label="header"
                  :value="header"
                />
              </el-select>
            </el-form-item>
          </el-form>

          <el-divider>数据预览</el-divider>
          <el-table :data="previewData" border size="small" max-height="300">
            <el-table-column
              v-for="header in headers"
              :key="header"
              :prop="header"
              :label="header"
            />
          </el-table>

          <div style="margin-top: 20px;">
            <el-button @click="currentStep = 0">上一步</el-button>
            <el-button type="primary" @click="currentStep = 2">下一步</el-button>
          </div>
        </div>

        <div v-show="currentStep === 2">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="文件名">{{ fileName }}</el-descriptions-item>
            <el-descriptions-item label="实体类型">{{ transformForm.entityType }}</el-descriptions-item>
            <el-descriptions-item label="名称列">{{ transformForm.nameColumn }}</el-descriptions-item>
            <el-descriptions-item label="属性列">{{ transformForm.propertyColumns?.join(', ') || '无' }}</el-descriptions-item>
            <el-descriptions-item label="预计导入数量">{{ totalRows }} 条</el-descriptions-item>
          </el-descriptions>

          <div style="margin-top: 20px;">
            <el-button @click="currentStep = 1">上一步</el-button>
            <el-button type="primary" @click="handleImport" :loading="importLoading">
              开始导入
            </el-button>
          </div>
        </div>
      </el-card>
    </template>

    <el-empty v-else description="请先选择项目" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import kgProject from '@/services/kgProject'
import kgEntityType from '@/services/kgEntityType'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const selectedProjectId = ref(null)
const projectList = ref([])
const currentStep = ref(0)
const previewLoading = ref(false)
const importLoading = ref(false)
const uploadRef = ref(null)
const selectedFile = ref(null)
const fileName = ref('')
const headers = ref([])
const previewData = ref([])
const totalRows = ref(0)
const entityTypes = ref([])

const transformForm = reactive({
  entityType: '',
  nameColumn: '',
  propertyColumns: []
})

const loadProjectList = async () => {
  try {
    const res = await kgProject.list({ pageNum: 1, pageSize: 100 })
    if (res.code === 200) {
      projectList.value = res.data.records
      if (route.params.projectId) {
        selectedProjectId.value = Number(route.params.projectId)
      }
    }
  } catch (error) {
    ElMessage.error('加载项目列表失败')
  }
}

const handleProjectChange = (projectId) => {
  if (projectId) {
    router.replace(`/kg/transform/${projectId}`)
    loadEntityTypes()
    currentStep.value = 0
  }
}

const loadEntityTypes = async () => {
  if (!selectedProjectId.value) return
  try {
    const res = await kgEntityType.listByProjectId(selectedProjectId.value)
    if (res.code === 200) {
      entityTypes.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载实体类型失败')
  }
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
  fileName.value = file.name
}

const handlePreview = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }

  previewLoading.value = true
  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    const res = await request({
      url: '/api/kg/transform/excel/preview',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    if (res.code === 200) {
      headers.value = res.data.headers
      previewData.value = res.data.previewData
      totalRows.value = res.data.totalRows
      currentStep.value = 1
    } else {
      ElMessage.error(res.message || '预览失败')
    }
  } catch (error) {
    ElMessage.error('预览失败')
  } finally {
    previewLoading.value = false
  }
}

const handleImport = async () => {
  if (!transformForm.entityType || !transformForm.nameColumn) {
    ElMessage.warning('请填写必填项')
    return
  }

  importLoading.value = true
  const formData = new FormData()
  formData.append('file', selectedFile.value)
  formData.append('projectId', selectedProjectId.value)
  formData.append('entityType', transformForm.entityType)
  formData.append('nameColumn', transformForm.nameColumn)
  formData.append('propertyColumns', transformForm.propertyColumns.join(','))

  const isCsv = fileName.value.endsWith('.csv')
  const url = isCsv ? '/api/kg/transform/csv' : '/api/kg/transform/excel'

  try {
    const res = await request({
      url: url,
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    if (res.code === 200) {
      ElMessage.success(res.data.message)
      router.push(`/kg/explore/${selectedProjectId.value}`)
    } else {
      ElMessage.error(res.message || '导入失败')
    }
  } catch (error) {
    ElMessage.error('导入失败')
  } finally {
    importLoading.value = false
  }
}

watch(selectedProjectId, (newVal) => {
  if (newVal) {
    loadEntityTypes()
  }
})

onMounted(() => {
  loadProjectList()
})
</script>

<style scoped>
.kg-transform {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}
</style>
