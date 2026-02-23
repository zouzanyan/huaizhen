<template>
  <div class="forum-board-management">
    <div class="page-header">
      <h2 class="page-title">论坛版块管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增版块</el-button>
    </div>

    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入名称或描述"
          clearable
          style="width: 200px"
        />
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          clearable
          style="width: 120px"
        >
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="版块名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="parentId" label="父版块" width="100">
          <template #default="{ row }">
            {{ row.parentId ? row.parentId : '-' }}
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
        <el-table-column label="操作" width="180" fixed="right">
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

    <!-- 新增/编辑版块弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="版块名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入版块名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="父版块" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父版块" clearable>
            <el-option
              v-for="forum in forumList"
              :key="forum.id"
              :label="forum.name"
              :value="forum.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import forumBoardService from '@/services/forumBoard'

const loading = ref(false)
const tableData = ref([])
const forumList = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  description: '',
  parentId: null,
  status: 1
})

const formRules = {
  name: [
    { required: true, message: '请输入版块名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => {
  return form.id ? '编辑版块' : '新增版块'
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

const loadData = async () => {
  loading.value = true
  try {
    const res = await forumBoardService.getBoardList({
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

const handleAdd = () => {
  form.id = null
  form.name = ''
  form.description = ''
  form.parentId = null
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.id = row.id
  form.name = row.name
  form.description = row.description
  form.parentId = row.parentId
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该版块吗？', '提示', {
      type: 'warning'
    })

    const res = await forumBoardService.deleteBoard(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()

  const data = {
    name: form.name,
    description: form.description,
    parentId: form.parentId,
    status: form.status
  }

  try {
    let res
    if (form.id) {
      data.id = form.id
      res = await forumBoardService.updateBoard(data)
    } else {
      res = await forumBoardService.createBoard(data)
    }

    if (res.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error(form.id ? '更新失败' : '创建失败')
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  loadForumList()
  loadData()
})
</script>

<style scoped>
.forum-board-management {
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
