<template>
  <div class="forum-comment-management">
    <div class="page-header">
      <h2 class="page-title">论坛评论管理</h2>
    </div>

    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入评论内容"
          clearable
          style="width: 200px"
        />
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          clearable
          style="width: 120px"
        >
          <el-option label="显示" :value="1" />
          <el-option label="隐藏" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button
          type="danger"
          :icon="Delete"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>

      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="authorName" label="评论者" width="120" />
        <el-table-column prop="postTitle" label="所属帖子" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '显示' : '隐藏' }}
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
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 编辑评论弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑评论"
      width="600px"
      @close="handleCloseEditDialog"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editFormRules"
        label-width="80px"
      >
        <el-form-item label="评论内容" prop="content">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入评论内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import forumCommentService from '@/services/forumComment'

const loading = ref(false)
const tableData = ref([])
const selectedIds = ref([])
const editDialogVisible = ref(false)
const editFormRef = ref(null)

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 编辑表单
const editForm = reactive({
  id: null,
  content: '',
  status: 1
})

const editFormRules = {
  content: [
    { required: true, message: '请输入评论内容', trigger: 'blur' },
    { min: 1, max: 1000, message: '评论内容长度在 1 到 1000 个字符', trigger: 'blur' }
  ]
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await forumCommentService.getCommentList({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      status: searchForm.status
    })
    if (res.code === 200) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.page = 1
  loadData()
}

const handlePageChange = () => {
  loadData()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 编辑评论
const handleEdit = async (row) => {
  try {
    const res = await forumCommentService.getCommentById(row.id)
    if (res.code === 200) {
      const comment = res.data
      Object.assign(editForm, {
        id: comment.id,
        content: comment.content,
        status: comment.status
      })
      editDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载评论信息失败')
  }
}

const handleCloseEditDialog = () => {
  editDialogVisible.value = false
  editFormRef.value?.resetFields()
}

const handleSubmitEdit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 先更新内容
        const contentRes = await forumCommentService.updateCommentContent(editForm.id, editForm.content)
        if (contentRes.code !== 200) {
          ElMessage.error(contentRes.message || '更新内容失败')
          return
        }

        // 再更新状态
        const statusRes = await forumCommentService.updateCommentStatus(editForm.id, editForm.status)
        if (statusRes.code === 200) {
          ElMessage.success('更新成功')
          editDialogVisible.value = false
          loadData()
        } else {
          ElMessage.error(statusRes.message || '更新状态失败')
        }
      } catch (error) {
        ElMessage.error('更新失败')
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该评论吗？删除后将无法恢复！', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })

    const res = await forumCommentService.deleteComment(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条评论吗？`, '提示', {
      type: 'warning'
    })

    const res = await forumCommentService.batchDeleteComments(selectedIds.value)
    if (res.code === 200) {
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.forum-comment-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
