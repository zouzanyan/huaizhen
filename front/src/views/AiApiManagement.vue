<template>
  <div class="ai-api-management">
    <div class="page-header">
      <h2 class="page-title">AI API 管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增配置</el-button>
    </div>

    <el-card>
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="配置名称" min-width="150" />
        <el-table-column prop="provider" label="提供商" width="120">
          <template #default="{ row }">
            <el-tag :type="getProviderTagType(row.provider)">{{ row.provider }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="modelName" label="模型名称" min-width="180" />
        <el-table-column prop="baseUrl" label="API URL" min-width="250">
          <template #default="{ row }">
            <el-tooltip :content="row.baseUrl" placement="top">
              <span class="url-preview">{{ row.baseUrl }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="apiKey" label="API Key" width="180">
          <template #default="{ row }">
            <span class="api-key-masked">{{ row.apiKey }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="maxTokens" label="Max Tokens" width="110" />
        <el-table-column prop="temperature" label="Temperature" width="110" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="提供商" prop="provider">
          <el-select v-model="form.provider" placeholder="请选择提供商" style="width: 100%">
            <el-option label="OpenAI" value="openai" />
            <el-option label="Anthropic" value="anthropic" />
            <el-option label="Google" value="google" />
            <el-option label="Azure" value="azure" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="API URL" prop="baseUrl">
          <el-input v-model="form.baseUrl" placeholder="请输入API基础URL" />
        </el-form-item>
        <el-form-item label="API Key" prop="apiKey">
          <el-input
            v-model="form.apiKey"
            type="password"
            placeholder="请输入API密钥"
            show-password
          />
        </el-form-item>
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="form.modelName" placeholder="请输入模型名称，如 gpt-4o" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Max Tokens" prop="maxTokens">
              <el-input-number v-model="form.maxTokens" :min="1" :max="128000" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Temperature" prop="temperature">
              <el-input-number
                v-model="form.temperature"
                :min="0"
                :max="2"
                :precision="2"
                :step="0.1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import aiApiService from '@/services/aiApi'

const loading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑配置' : '新增配置')
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  provider: 'openai',
  baseUrl: '',
  apiKey: '',
  modelName: '',
  maxTokens: 4096,
  temperature: 0.7,
  status: 1,
  remark: ''
})

const formRules = {
  name: [
    { required: true, message: '请输入配置名称', trigger: 'blur' }
  ],
  provider: [
    { required: true, message: '请选择提供商', trigger: 'change' }
  ],
  baseUrl: [
    { required: true, message: '请输入API URL', trigger: 'blur' }
  ],
  apiKey: [
    { required: true, message: '请输入API Key', trigger: 'blur' }
  ],
  modelName: [
    { required: true, message: '请输入模型名称', trigger: 'blur' }
  ]
}

const providerTagMap = {
  openai: 'success',
  anthropic: 'warning',
  google: 'primary',
  azure: 'info',
  custom: ''
}

const getProviderTagType = (provider) => {
  return providerTagMap[provider] || ''
}

const getAiApiList = async () => {
  loading.value = true
  try {
    const res = await aiApiService.getAiApiList({ page: 1, size: 100 })
    if (res.code === 200) {
      tableData.value = res.data.list || []
    }
  } catch (error) {
    ElMessage.error(error.message || '获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.id = null
  form.name = ''
  form.provider = 'openai'
  form.baseUrl = ''
  form.apiKey = ''
  form.modelName = ''
  form.maxTokens = 4096
  form.temperature = 0.7
  form.status = 1
  form.remark = ''
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  try {
    const res = await aiApiService.getAiApiById(row.id)
    if (res.code === 200) {
      const data = res.data
      form.id = data.id
      form.name = data.name
      form.provider = data.provider
      form.baseUrl = data.baseUrl
      form.apiKey = data.apiKey
      form.modelName = data.modelName
      form.maxTokens = data.maxTokens
      form.temperature = data.temperature
      form.status = data.status
      form.remark = data.remark
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error(error.message || '获取详情失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该配置吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await aiApiService.deleteAiApi(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getAiApiList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

const handleStatusChange = async (row) => {
  try {
    const res = await aiApiService.updateStatus(row.id, row.status)
    if (res.code === 200) {
      ElMessage.success(row.status === 1 ? '启用成功' : '禁用成功')
    } else {
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error(error.message || '操作失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = form.id ? aiApiService.updateAiApi : aiApiService.addAiApi
        const res = await api(form)
        if (res.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          getAiApiList()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  getAiApiList()
})
</script>

<style scoped>
.ai-api-management {
  height: 100%;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 500;
  color: #111827;
  margin: 0;
}

.url-preview {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.api-key-masked {
  font-family: monospace;
  color: #909399;
}
</style>
