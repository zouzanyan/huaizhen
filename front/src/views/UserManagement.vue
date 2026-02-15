<template>
  <div class="user-management">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
    </div>

    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入用户名"
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
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="username" label="用户名" min-width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="200">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" min-width="200">
          <template #default="{ row }">
            {{ formatDateTime(row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" :icon="Key" @click="handleAssignRoles(row)">角色</el-button>
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

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item
          v-if="!form.id"
          label="密码"
          prop="password"
        >
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item
          v-else
          label="密码"
          prop="password"
        >
          <el-input
            v-model="form.password"
            type="password"
            placeholder="不填写则不修改密码"
            show-password
          />
          <div class="form-tip">编辑时留空则不修改密码</div>
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

    <!-- 分配角色弹窗 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="400px"
      @close="handleRoleDialogClose"
    >
      <el-form label-width="80px">
        <el-form-item label="用户">
          <span>{{ currentUser?.username }}</span>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="selectedRoleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
          </el-select>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Search, Refresh, Edit, Delete, Key } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import userService from '@/services/user'
import roleService from '@/services/role'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑用户' : '新增用户')
const formRef = ref(null)
const form = reactive({
  id: null,
  username: '',
  password: null,
  status: 1
})

const formRules = computed(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: getPasswordRules(),
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}))

// 动态生成密码验证规则
const getPasswordRules = () => {
  // 新增用户时，密码必填
  // 编辑用户时，密码可选（如果不填写则不修改密码）
  if (!form.id) {
    return [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ]
  }
  // 编辑用户时，密码可选
  return []
}

const roleDialogVisible = ref(false)
const currentUser = ref(null)
const roleList = ref([])
const selectedRoleIds = ref([])

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const res = await userService.getUserList(params)
    if (res.code === 200) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error(error.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  pagination.page = 1
  getUserList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.page = 1
  getUserList()
}

// 分页改变
const handlePageChange = () => {
  getUserList()
}

// 新增用户
const handleAdd = () => {
  form.id = null
  form.username = ''
  form.password = null
  form.status = 1
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
  form.id = row.id
  form.username = row.username
  form.password = null  // 编辑时密码初始化为空，不修改密码
  form.status = row.status
  dialogVisible.value = true
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await userService.deleteUser(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getUserList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 编辑用户且密码为空时，不传递密码字段
        let submitData = { ...form }
        if (form.id && !submitData.password) {
          delete submitData.password
        }

        const api = form.id ? userService.updateUser : userService.addUser
        const res = await api(submitData)
        if (res.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          getUserList()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

// 弹窗关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 分配角色
const handleAssignRoles = async (row) => {
  currentUser.value = row
  roleDialogVisible.value = true

  // 获取所有角色
  try {
    const res = await roleService.getAllRoles()
    if (res.code === 200) {
      roleList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  }

  // 获取用户当前角色
  try {
    const res = await userService.getUserRoles(row.id)
    if (res.code === 200) {
      selectedRoleIds.value = res.data.map(item => item.id)
    }
  } catch (error) {
    ElMessage.error('获取用户角色失败')
  }
}

// 提交角色分配
const handleRoleSubmit = async () => {
  try {
    const res = await userService.assignUserRoles(currentUser.value.id, selectedRoleIds.value)
    if (res.code === 200) {
      ElMessage.success('分配角色成功')
      roleDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '分配角色失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '分配角色失败')
  }
}

// 角色弹窗关闭
const handleRoleDialogClose = () => {
  currentUser.value = null
  roleList.value = []
  selectedRoleIds.value = []
}

// 格式化日期时间
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
  getUserList()
})
</script>

<style scoped>
.user-management {
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

.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
