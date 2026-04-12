<template>
  <div class="kg-model-management">
    <div class="page-header">
      <span class="title">图谱模型管理</span>
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
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>实体类型</span>
                <el-button type="primary" size="small" @click="handleAddEntityType">添加</el-button>
              </div>
            </template>
            <el-table :data="entityTypes" v-loading="entityLoading" size="small">
              <el-table-column prop="name" label="名称" />
              <el-table-column prop="color" label="颜色" width="100">
                <template #default="{ row }">
                  <div :style="{ backgroundColor: row.color, width: '30px', height: '20px', borderRadius: '4px' }"></div>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述" show-overflow-tooltip />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="primary" link size="small" @click="handleEditEntityType(row)">编辑</el-button>
                  <el-button type="danger" link size="small" @click="handleDeleteEntityType(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>关系类型</span>
                <el-button type="primary" size="small" @click="handleAddRelationType">添加</el-button>
              </div>
            </template>
            <el-table :data="relationTypes" v-loading="relationLoading" size="small">
              <el-table-column prop="name" label="名称" />
              <el-table-column prop="description" label="描述" show-overflow-tooltip />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="primary" link size="small" @click="handleEditRelationType(row)">编辑</el-button>
                  <el-button type="danger" link size="small" @click="handleDeleteRelationType(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>

      <el-dialog v-model="entityDialogVisible" :title="entityDialogTitle" width="400">
        <el-form :model="entityForm" label-width="80px">
          <el-form-item label="名称" required>
            <el-input v-model="entityForm.name" placeholder="请输入实体类型名称" />
          </el-form-item>
          <el-form-item label="颜色">
            <el-color-picker v-model="entityForm.color" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="entityForm.description" placeholder="请输入描述" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="entityDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitEntityType">确定</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="relationDialogVisible" :title="relationDialogTitle" width="400">
        <el-form :model="relationForm" label-width="80px">
          <el-form-item label="名称" required>
            <el-input v-model="relationForm.name" placeholder="请输入关系类型名称" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="relationForm.description" placeholder="请输入描述" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="relationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitRelationType">确定</el-button>
        </template>
      </el-dialog>
    </template>

    <el-empty v-else description="请先选择项目" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import kgProject from '@/services/kgProject'
import kgEntityType from '@/services/kgEntityType'
import kgRelationType from '@/services/kgRelationType'

const route = useRoute()
const router = useRouter()
const selectedProjectId = ref(null)
const projectList = ref([])
const entityTypes = ref([])
const relationTypes = ref([])
const entityLoading = ref(false)
const relationLoading = ref(false)
const entityDialogVisible = ref(false)
const entityDialogTitle = ref('添加实体类型')
const relationDialogVisible = ref(false)
const relationDialogTitle = ref('添加关系类型')

const entityForm = reactive({
  id: null,
  projectId: null,
  name: '',
  color: '#409EFF',
  description: ''
})

const relationForm = reactive({
  id: null,
  projectId: null,
  name: '',
  description: ''
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
    router.replace(`/kg/model/${projectId}`)
    loadEntityTypes()
    loadRelationTypes()
  }
}

const loadEntityTypes = async () => {
  if (!selectedProjectId.value) return
  entityLoading.value = true
  try {
    const res = await kgEntityType.listByProjectId(selectedProjectId.value)
    if (res.code === 200) {
      entityTypes.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载实体类型失败')
  } finally {
    entityLoading.value = false
  }
}

const loadRelationTypes = async () => {
  if (!selectedProjectId.value) return
  relationLoading.value = true
  try {
    const res = await kgRelationType.listByProjectId(selectedProjectId.value)
    if (res.code === 200) {
      relationTypes.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载关系类型失败')
  } finally {
    relationLoading.value = false
  }
}

const handleAddEntityType = () => {
  entityDialogTitle.value = '添加实体类型'
  entityForm.id = null
  entityForm.projectId = selectedProjectId.value
  entityForm.name = ''
  entityForm.color = '#409EFF'
  entityForm.description = ''
  entityDialogVisible.value = true
}

const handleEditEntityType = (row) => {
  entityDialogTitle.value = '编辑实体类型'
  Object.assign(entityForm, row)
  entityDialogVisible.value = true
}

const handleSubmitEntityType = async () => {
  if (!entityForm.name) {
    ElMessage.warning('请输入实体类型名称')
    return
  }
  entityForm.projectId = selectedProjectId.value
  try {
    if (entityForm.id) {
      await kgEntityType.update(entityForm)
    } else {
      await kgEntityType.create(entityForm)
    }
    ElMessage.success('操作成功')
    entityDialogVisible.value = false
    loadEntityTypes()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDeleteEntityType = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该实体类型吗？', '提示', { type: 'warning' })
    await kgEntityType.delete(row.id)
    ElMessage.success('删除成功')
    loadEntityTypes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleAddRelationType = () => {
  relationDialogTitle.value = '添加关系类型'
  relationForm.id = null
  relationForm.projectId = selectedProjectId.value
  relationForm.name = ''
  relationForm.description = ''
  relationDialogVisible.value = true
}

const handleEditRelationType = (row) => {
  relationDialogTitle.value = '编辑关系类型'
  Object.assign(relationForm, row)
  relationDialogVisible.value = true
}

const handleSubmitRelationType = async () => {
  if (!relationForm.name) {
    ElMessage.warning('请输入关系类型名称')
    return
  }
  relationForm.projectId = selectedProjectId.value
  try {
    if (relationForm.id) {
      await kgRelationType.update(relationForm)
    } else {
      await kgRelationType.create(relationForm)
    }
    ElMessage.success('操作成功')
    relationDialogVisible.value = false
    loadRelationTypes()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDeleteRelationType = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该关系类型吗？', '提示', { type: 'warning' })
    await kgRelationType.delete(row.id)
    ElMessage.success('删除成功')
    loadRelationTypes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

watch(selectedProjectId, (newVal) => {
  if (newVal) {
    loadEntityTypes()
    loadRelationTypes()
  }
})

onMounted(() => {
  loadProjectList()
})
</script>

<style scoped>
.kg-model-management {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
