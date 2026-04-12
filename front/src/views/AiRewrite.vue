<template>
  <div class="ai-rewrite-management">
    <div class="page-header">
      <h2 class="page-title">AI 仿写</h2>
    </div>

    <div class="content-wrapper">
      <el-card class="form-card">
        <template #header>
          <span>仿写配置</span>
        </template>
        <el-form :model="form" label-width="100px">
          <el-form-item label="标题">
            <el-input v-model="form.title" placeholder="请输入标题（可选）" />
          </el-form-item>
          <el-form-item label="提示词" required>
            <el-select v-model="form.promptId" placeholder="请选择提示词" style="width: 100%">
              <el-option-group v-for="category in promptCategories" :key="category.id" :label="category.name">
                <el-option
                  v-for="prompt in category.prompts"
                  :key="prompt.id"
                  :label="prompt.title"
                  :value="prompt.id"
                />
              </el-option-group>
            </el-select>
          </el-form-item>
          <el-form-item label="API配置" required>
            <el-select v-model="form.apiId" placeholder="请选择API配置" style="width: 100%">
              <el-option
                v-for="api in apiList"
                :key="api.id"
                :label="`${api.name} (${api.modelName})`"
                :value="api.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="原文" required>
            <el-input
              v-model="form.originalText"
              type="textarea"
              :rows="8"
              placeholder="请输入需要仿写的原文"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleExecute" :loading="executing" :disabled="!canExecute">
              {{ executing ? '处理中...' : '开始仿写' }}
            </el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="result-card">
        <template #header>
          <div class="card-header">
            <span>仿写结果</span>
            <el-button v-if="result" type="primary" size="small" @click="copyResult">复制结果</el-button>
          </div>
        </template>
        <div v-if="result" class="result-content">
          <pre>{{ result }}</pre>
        </div>
        <el-empty v-else description="暂无结果" />
      </el-card>
    </div>

    <el-card class="history-card">
      <template #header>
        <div class="card-header">
          <span>历史记录</span>
          <el-button type="danger" size="small" @click="handleBatchDelete" :disabled="selectedRows.length === 0">
            批量删除
          </el-button>
        </div>
      </template>
      <el-table :data="historyList" style="width: 100%" v-loading="historyLoading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="120">
          <template #default="{ row }">
            {{ row.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="originalText" label="原文" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.originalText" placement="top">
              <span class="text-preview">{{ row.originalText }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="modelName" label="模型" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">查看</el-button>
            <el-button link type="warning" @click="handleRetry(row)" :disabled="row.status === 1">重试</el-button>
            <el-button link type="danger" @click="handleDelete(row)" :disabled="row.status === 1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ currentDetail?.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="模型">{{ currentDetail?.modelName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentDetail?.status)">
            {{ getStatusText(currentDetail?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(currentDetail?.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="原文" :span="2">
          <pre class="detail-text">{{ currentDetail?.originalText }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="结果" :span="2">
          <pre class="detail-text">{{ currentDetail?.resultText || currentDetail?.errorMsg || '-' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import aiRewriteService from '@/services/aiRewrite'
import aiApiService from '@/services/aiApi'
import promptService from '@/services/prompt'

const executing = ref(false)
const result = ref('')

const form = reactive({
  title: '',
  promptId: null,
  apiId: null,
  originalText: ''
})

const promptCategories = ref([])
const apiList = ref([])

const historyLoading = ref(false)
const historyList = ref([])
const selectedRows = ref([])

const detailDialogVisible = ref(false)
const currentDetail = ref(null)

const canExecute = computed(() => {
  return form.apiId && form.originalText
})

const getStatusTagType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待处理', 1: '处理中', 2: '成功', 3: '失败' }
  return map[status] || '未知'
}

const loadPrompts = async () => {
  try {
    const res = await promptService.getAllCategories()
    if (res.code === 200) {
      const categories = res.data || []
      for (const category of categories) {
        const promptRes = await promptService.getPromptList({ categoryId: category.id, page: 1, size: 100 })
        if (promptRes.code === 200) {
          category.prompts = promptRes.data?.list || []
        }
      }
      promptCategories.value = categories
    }
  } catch (error) {
    console.error('加载提示词失败', error)
  }
}

const loadApiList = async () => {
  try {
    const res = await aiApiService.getAllAiApis()
    if (res.code === 200) {
      apiList.value = res.data || []
    }
  } catch (error) {
    console.error('加载API配置失败', error)
  }
}

const loadHistory = async () => {
  historyLoading.value = true
  try {
    const res = await aiRewriteService.getRewriteList({ page: 1, size: 20 })
    if (res.code === 200) {
      historyList.value = res.data?.list || []
    }
  } catch (error) {
    ElMessage.error(error.message || '加载历史记录失败')
  } finally {
    historyLoading.value = false
  }
}

const handleExecute = async () => {
  if (!form.apiId) {
    ElMessage.warning('请选择API配置')
    return
  }
  if (!form.originalText) {
    ElMessage.warning('请输入原文')
    return
  }

  executing.value = true
  result.value = ''

  try {
    const res = await aiRewriteService.executeAndSave(form)
    if (res.code === 200) {
      result.value = res.data.resultText
      if (res.data.status === 2) {
        ElMessage.success('仿写成功')
      } else if (res.data.status === 3) {
        ElMessage.error(res.data.errorMsg || '仿写失败')
      }
      loadHistory()
    } else {
      ElMessage.error(res.message || '仿写失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '仿写失败')
  } finally {
    executing.value = false
  }
}

const handleReset = () => {
  form.title = ''
  form.promptId = null
  form.apiId = null
  form.originalText = ''
  result.value = ''
}

const copyResult = async () => {
  try {
    await navigator.clipboard.writeText(result.value)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const handleViewDetail = (row) => {
  currentDetail.value = row
  detailDialogVisible.value = true
}

const handleRetry = async (row) => {
  try {
    const res = await aiRewriteService.executeRewrite(row.id)
    if (res.code === 200) {
      if (res.data.status === 2) {
        ElMessage.success('重试成功')
      } else if (res.data.status === 3) {
        ElMessage.error(res.data.errorMsg || '重试失败')
      }
      loadHistory()
    } else {
      ElMessage.error(res.message || '重试失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '重试失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        const res = await aiRewriteService.deleteRewrite(row.id)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          loadHistory()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
      }
    })
    .catch(() => {})
}

const handleBatchDelete = () => {
  const ids = selectedRows.value.map(row => row.id)
  ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 条记录吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        const res = await aiRewriteService.batchDeleteRewrites(ids)
        if (res.code === 200) {
          ElMessage.success('批量删除成功')
          loadHistory()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
      }
    })
    .catch(() => {})
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
  loadPrompts()
  loadApiList()
  loadHistory()
})
</script>

<style scoped>
.ai-rewrite-management {
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

.content-wrapper {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-card {
  flex: 1;
}

.result-card {
  flex: 1;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.result-content {
  max-height: 400px;
  overflow-y: auto;
}

.result-content pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  font-family: inherit;
  line-height: 1.6;
}

.history-card {
  margin-top: 20px;
}

.text-preview {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.detail-text {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  font-family: inherit;
  max-height: 200px;
  overflow-y: auto;
}
</style>
