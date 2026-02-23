<template>
  <div class="forum-user-management">
    <div class="page-header">
      <h2 class="page-title">论坛用户管理</h2>
    </div>

    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入用户名或邮箱"
          clearable
          style="width: 200px"
        />
        <el-select
          v-model="searchForm.role"
          placeholder="角色"
          clearable
          style="width: 120px"
        >
          <el-option label="管理员" :value="1" />
          <el-option label="普通用户" :value="0" />
        </el-select>
        <el-select
          v-model="searchForm.status"
          placeholder="状态"
          clearable
          style="width: 120px"
        >
          <el-option label="正常" :value="1" />
          <el-option label="已封禁" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'warning' : 'info'">
              {{ row.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '已封禁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">查看</el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '封禁' : '解封' }}
            </el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEditRole(row)">角色</el-button>
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

    <!-- 查看详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="用户详情"
      width="600px"
    >
      <div v-if="currentUser">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="currentUser.role === 1 ? 'warning' : 'info'">
              {{ currentUser.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
              {{ currentUser.status === 1 ? '正常' : '已封禁' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间" :span="2">
            {{ formatDateTime(currentUser.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 编辑角色弹窗 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="修改角色"
      width="400px"
    >
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="roleForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="roleForm.role">
            <el-radio :value="0">普通用户</el-radio>
            <el-radio :value="1">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRoleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Edit } from '@element-plus/icons-vue'
import forumUserService from '@/services/forumUser'

const loading = ref(false)
const tableData = ref([])
const detailDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const currentUser = ref(null)

const searchForm = reactive({
  keyword: '',
  role: null,
  status: null
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const roleForm = reactive({
  id: null,
  username: '',
  role: 0
})

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await forumUserService.getUserList({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      role: searchForm.role,
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
  searchForm.role = null
  searchForm.status = null
  pagination.page = 1
  loadData()
}

const handlePageChange = () => {
  loadData()
}

const handleView = async (row) => {
  try {
    const res = await forumUserService.getUserById(row.id)
    if (res.code === 200) {
      currentUser.value = res.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
  }
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '封禁' : '解封'

  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      type: 'warning'
    })

    const res = await forumUserService.updateUserStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleEditRole = (row) => {
  roleForm.id = row.id
  roleForm.username = row.username
  roleForm.role = row.role
  roleDialogVisible.value = true
}

const handleRoleSubmit = async () => {
  try {
    const res = await forumUserService.updateUserRole(roleForm.id, roleForm.role)
    if (res.code === 200) {
      ElMessage.success('修改角色成功')
      roleDialogVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error('修改角色失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.forum-user-management {
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
