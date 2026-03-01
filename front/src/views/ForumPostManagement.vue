<template>
  <div class="forum-post-management">
    <div class="page-header">
      <h2 class="page-title">论坛帖子管理</h2>
    </div>

    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入标题或内容"
          clearable
          style="width: 200px"
        />
        <el-select
          v-model="searchForm.forumId"
          placeholder="请选择版块"
          clearable
          style="width: 150px"
        >
          <el-option
            v-for="forum in forumList"
            :key="forum.id"
            :label="forum.name"
            :value="forum.id"
          />
        </el-select>
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          clearable
          style="width: 120px"
        >
          <el-option label="正常" :value="1" />
          <el-option label="已删除" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button type="success" :icon="Plus" @click="handleShowCreateDialog">新增贴文</el-button>
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
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="forumName" label="版块" width="150" />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '已删除' }}
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

    <!-- 创建/编辑弹窗 -->
    <el-dialog
      v-model="createDialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleCloseCreateDialog"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createFormRules"
        label-width="80px"
      >
        <el-form-item label="版块" prop="forumId">
          <el-select
            v-model="createForm.forumId"
            placeholder="请选择版块"
            style="width: 100%"
          >
            <el-option
              v-for="forum in forumList"
              :key="forum.id"
              :label="forum.name"
              :value="forum.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作者" prop="userId">
          <el-select
            v-model="createForm.userId"
            placeholder="请选择作者"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.username"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="createForm.title"
            placeholder="请输入标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="createForm.content"
            type="textarea"
            placeholder="请输入内容"
            :rows="8"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="内容类型">
          <el-radio-group v-model="createForm.contentType">
            <el-radio :value="0">普通文本</el-radio>
            <el-radio :value="1">Markdown</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="createForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">已删除</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleCloseCreateDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmitPost">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue'
import forumPostService from '@/services/forumPost'
import forumBoardService from '@/services/forumBoard'
import forumUserService from '@/services/forumUser'

const loading = ref(false)
const tableData = ref([])
const forumList = ref([])
const userList = ref([])
const selectedIds = ref([])
const createDialogVisible = ref(false)

// 创建/编辑表单
const createFormRef = ref()
const createForm = reactive({
  id: null,
  forumId: null,
  userId: null,
  title: '',
  content: '',
  contentType: 0,
  status: 1
})

const dialogTitle = computed(() => createForm.id ? '编辑贴文' : '新增贴文')

const createFormRules = {
  forumId: [{ required: true, message: '请选择版块', trigger: 'change' }],
  userId: [{ required: true, message: '请选择作者', trigger: 'change' }],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 5, message: '内容至少 5 个字符', trigger: 'blur' }
  ]
}

const searchForm = reactive({
  keyword: '',
  forumId: null,
  status: null
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const loadForumList = async () => {
  try {
    const res = await forumBoardService.getAllBoards()
    if (res.code === 200) {
      forumList.value = res.data || []
    }
  } catch (error) {
    console.error('加载版块列表失败:', error)
  }
}

const loadUserList = async () => {
  try {
    const res = await forumUserService.getUserList({ page: 1, size: 1000 })
    if (res.code === 200) {
      userList.value = res.data.list || []
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await forumPostService.getPostList({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      forumId: searchForm.forumId,
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
  searchForm.forumId = null
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

// 编辑帖子
const handleEdit = async (row) => {
  try {
    const res = await forumPostService.getPostById(row.id)
    if (res.code === 200) {
      const post = res.data
      Object.assign(createForm, {
        id: post.id,
        forumId: post.forumId,
        userId: post.userId,
        title: post.title,
        content: post.content,
        contentType: post.contentType,
        status: post.status
      })
      createDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载帖子信息失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该帖子吗？删除后将无法恢复！', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })

    const res = await forumPostService.updatePostStatus(row.id, 0)
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
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个帖子吗？`, '提示', {
      type: 'warning'
    })

    const res = await forumPostService.batchDeletePosts(selectedIds.value)
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

const handleShowCreateDialog = () => {
  Object.assign(createForm, {
    id: null,
    forumId: null,
    userId: null,
    title: '',
    content: '',
    contentType: 0,
    status: 1
  })
  createDialogVisible.value = true
}

const handleCloseCreateDialog = () => {
  createDialogVisible.value = false
  createFormRef.value?.resetFields()
}

// 提交创建或编辑
const handleSubmitPost = async () => {
  try {
    await createFormRef.value.validate()

    const api = createForm.id ? forumPostService.updatePost : forumPostService.createPost
    const res = await api(createForm)

    if (res.code === 200) {
      ElMessage.success(createForm.id ? '更新成功' : '创建成功')
      createDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadForumList()
  loadUserList()
  loadData()
})
</script>

<style scoped>
.forum-post-management {
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

.post-content {
  max-height: 300px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
