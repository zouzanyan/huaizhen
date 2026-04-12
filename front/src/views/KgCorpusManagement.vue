<template>
  <div class="kg-corpus-management">
    <div class="page-header">
      <span class="title">语料管理</span>
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
        <template #header>
          <div class="card-header">
            <span>语料列表</span>
            <el-button type="primary" @click="handleAdd">添加语料</el-button>
          </div>
        </template>

        <el-table :data="corpusList" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
          <el-table-column prop="source" label="来源" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'info' : 'success'">
                {{ row.status === 1 ? '未处理' : '已处理' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleExtract(row)">抽取知识</el-button>
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600">
        <el-form :model="form" label-width="80px">
          <el-form-item label="标题">
            <el-input v-model="form.title" placeholder="请输入语料标题" />
          </el-form-item>
          <el-form-item label="内容" required>
            <el-input
              v-model="form.content"
              type="textarea"
              rows="8"
              placeholder="请输入语料内容"
            />
          </el-form-item>
          <el-form-item label="来源">
            <el-input v-model="form.source" placeholder="请输入来源" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
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
import kgCorpus from '@/services/kgCorpus'

const route = useRoute()
const router = useRouter()
const selectedProjectId = ref(null)
const projectList = ref([])
const corpusList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加语料')

const form = reactive({
  id: null,
  projectId: null,
  title: '',
  content: '',
  source: '',
  status: 1
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
    router.replace(`/kg/corpus/${projectId}`)
    loadData()
  }
}

const loadData = async () => {
  if (!selectedProjectId.value) return
  loading.value = true
  try {
    const res = await kgCorpus.listByProjectId(selectedProjectId.value)
    if (res.code === 200) {
      corpusList.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加语料'
  form.id = null
  form.projectId = selectedProjectId.value
  form.title = ''
  form.content = ''
  form.source = ''
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑语料'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.content) {
    ElMessage.warning('请输入语料内容')
    return
  }
  form.projectId = selectedProjectId.value
  try {
    if (form.id) {
      await kgCorpus.update(form)
    } else {
      await kgCorpus.create(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该语料吗？', '提示', { type: 'warning' })
    await kgCorpus.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleExtract = (row) => {
  router.push({
    path: `/kg/explore/${selectedProjectId.value}`,
    query: { text: row.content }
  })
}

watch(selectedProjectId, (newVal) => {
  if (newVal) {
    loadData()
  }
})

onMounted(() => {
  loadProjectList()
})
</script>

<style scoped>
.kg-corpus-management {
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
