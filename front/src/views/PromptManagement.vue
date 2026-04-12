<template>
  <div class="prompt-management">
    <div class="page-header">
      <h2 class="page-title">提示词管理</h2>
    </div>

    <div class="content-wrapper">
      <el-card class="category-card">
        <template #header>
          <div class="card-header">
            <span>分类列表</span>
            <el-button type="primary" :icon="Plus" size="small" @click="handleAddCategory">新增分类</el-button>
          </div>
        </template>
        <div class="category-list" v-loading="categoryLoading">
          <div
            v-for="item in categoryList"
            :key="item.id"
            :class="['category-item', { active: currentCategory?.id === item.id }]"
            @click="selectCategory(item)"
          >
            <span class="category-name">{{ item.name }}</span>
            <span class="category-count">({{ item.promptCount || 0 }})</span>
            <div class="category-actions">
              <el-button link type="primary" size="small" :icon="Edit" @click.stop="handleEditCategory(item)"></el-button>
              <el-button link type="danger" size="small" :icon="Delete" @click.stop="handleDeleteCategory(item)"></el-button>
            </div>
          </div>
          <el-empty v-if="!categoryLoading && categoryList.length === 0" description="暂无分类" />
        </div>
      </el-card>

      <el-card class="prompt-card">
        <template #header>
          <div class="card-header">
            <span>{{ currentCategory ? currentCategory.name + ' - 提示词列表' : '提示词列表' }}</span>
            <el-button type="primary" :icon="Plus" size="small" @click="handleAddPrompt" :disabled="!currentCategory">新增提示词</el-button>
          </div>
        </template>
        <el-table :data="promptList" style="width: 100%" v-loading="promptLoading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="150" />
          <el-table-column prop="content" label="内容" min-width="300">
            <template #default="{ row }">
              <el-tooltip :content="row.content" placement="top" :disabled="row.content?.length < 50">
                <span class="content-preview">{{ row.content }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" :icon="Edit" @click="handleEditPrompt(row)">编辑</el-button>
              <el-button link type="danger" :icon="Delete" @click="handleDeletePrompt(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!promptLoading && promptList.length === 0 && currentCategory" description="暂无提示词" />
        <el-empty v-if="!currentCategory" description="请先选择分类" />
      </el-card>
    </div>

    <el-dialog v-model="categoryDialogVisible" :title="categoryDialogTitle" width="400px" @close="handleCategoryDialogClose">
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCategorySubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="promptDialogVisible" :title="promptDialogTitle" width="600px" @close="handlePromptDialogClose">
      <el-form ref="promptFormRef" :model="promptForm" :rules="promptRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="promptForm.title" placeholder="请输入提示词标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="promptForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入提示词内容"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="promptForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="promptDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePromptSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import promptService from '@/services/prompt'

const categoryLoading = ref(false)
const categoryList = ref([])
const currentCategory = ref(null)

const promptLoading = ref(false)
const promptList = ref([])

const categoryDialogVisible = ref(false)
const categoryDialogTitle = computed(() => categoryForm.id ? '编辑分类' : '新增分类')
const categoryFormRef = ref(null)
const categoryForm = reactive({
  id: null,
  name: '',
  sort: 0
})

const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

const promptDialogVisible = ref(false)
const promptDialogTitle = computed(() => promptForm.id ? '编辑提示词' : '新增提示词')
const promptFormRef = ref(null)
const promptForm = reactive({
  id: null,
  categoryId: null,
  title: '',
  content: '',
  status: 1
})

const promptRules = {
  title: [
    { required: true, message: '请输入提示词标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入提示词内容', trigger: 'blur' }
  ]
}

const getCategoryList = async () => {
  categoryLoading.value = true
  try {
    const res = await promptService.getAllCategories()
    if (res.code === 200) {
      categoryList.value = res.data || []
      if (categoryList.value.length > 0 && !currentCategory.value) {
        selectCategory(categoryList.value[0])
      }
    }
  } catch (error) {
    ElMessage.error(error.message || '获取分类列表失败')
  } finally {
    categoryLoading.value = false
  }
}

const selectCategory = (category) => {
  currentCategory.value = category
  getPromptList()
}

const getPromptList = async () => {
  if (!currentCategory.value) {
    promptList.value = []
    return
  }
  promptLoading.value = true
  try {
    const res = await promptService.getPromptList({
      categoryId: currentCategory.value.id,
      page: 1,
      size: 100
    })
    if (res.code === 200) {
      promptList.value = res.data?.list || []
    }
  } catch (error) {
    ElMessage.error(error.message || '获取提示词列表失败')
  } finally {
    promptLoading.value = false
  }
}

const handleAddCategory = () => {
  categoryForm.id = null
  categoryForm.name = ''
  categoryForm.sort = 0
  categoryDialogVisible.value = true
}

const handleEditCategory = (row) => {
  categoryForm.id = row.id
  categoryForm.name = row.name
  categoryForm.sort = row.sort || 0
  categoryDialogVisible.value = true
}

const handleDeleteCategory = (row) => {
  ElMessageBox.confirm('删除分类将同时删除该分类下的所有提示词，确定要删除吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await promptService.deleteCategory(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        if (currentCategory.value?.id === row.id) {
          currentCategory.value = null
          promptList.value = []
        }
        getCategoryList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

const handleCategorySubmit = async () => {
  if (!categoryFormRef.value) return
  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = categoryForm.id ? promptService.updateCategory : promptService.addCategory
        const res = await api(categoryForm)
        if (res.code === 200) {
          ElMessage.success(categoryForm.id ? '更新成功' : '新增成功')
          categoryDialogVisible.value = false
          getCategoryList()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleCategoryDialogClose = () => {
  categoryFormRef.value?.resetFields()
}

const handleAddPrompt = () => {
  promptForm.id = null
  promptForm.categoryId = currentCategory.value.id
  promptForm.title = ''
  promptForm.content = ''
  promptForm.status = 1
  promptDialogVisible.value = true
}

const handleEditPrompt = (row) => {
  promptForm.id = row.id
  promptForm.categoryId = row.categoryId
  promptForm.title = row.title
  promptForm.content = row.content
  promptForm.status = row.status
  promptDialogVisible.value = true
}

const handleDeletePrompt = (row) => {
  ElMessageBox.confirm('确定要删除该提示词吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await promptService.deletePrompt(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getPromptList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

const handlePromptSubmit = async () => {
  if (!promptFormRef.value) return
  await promptFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = promptForm.id ? promptService.updatePrompt : promptService.addPrompt
        const res = await api(promptForm)
        if (res.code === 200) {
          ElMessage.success(promptForm.id ? '更新成功' : '新增成功')
          promptDialogVisible.value = false
          getPromptList()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handlePromptDialogClose = () => {
  promptFormRef.value?.resetFields()
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
  getCategoryList()
})
</script>

<style scoped>
.prompt-management {
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
  height: calc(100% - 60px);
}

.category-card {
  width: 280px;
  flex-shrink: 0;
}

.prompt-card {
  flex: 1;
  min-width: 0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.category-list {
  max-height: 500px;
  overflow-y: auto;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.category-item:hover {
  background-color: #f5f7fa;
}

.category-item.active {
  background-color: #ecf5ff;
}

.category-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-count {
  color: #909399;
  font-size: 12px;
  margin-right: 8px;
}

.category-actions {
  opacity: 0;
  transition: opacity 0.2s;
}

.category-item:hover .category-actions {
  opacity: 1;
}

.content-preview {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
