<template>
  <div class="role-management">
    <div class="page-header">
      <h2 class="page-title">角色管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
    </div>

    <el-card>
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="角色名称" min-width="180" />
        <el-table-column prop="code" label="角色编码" min-width="200" />
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" :icon="Key" @click="handleAssignMenus(row)">菜单权限</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑角色弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
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

    <!-- 菜单权限弹窗 -->
    <el-dialog
      v-model="menuDialogVisible"
      title="菜单权限配置"
      width="500px"
      @close="handleMenuDialogClose"
    >
      <el-form label-width="80px">
        <el-form-item label="角色">
          <span>{{ currentRole?.name }}（{{ currentRole?.code }}）</span>
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-tree
            ref="menuTreeRef"
            :data="menuTree"
            :props="treeProps"
            node-key="id"
            show-checkbox
            default-expand-all
            :default-checked-keys="checkedMenuIds"
            style="border: 1px solid #dcdfe6; padding: 10px; border-radius: 4px;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMenuSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Edit, Delete, Key } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import roleService from '@/services/role'
import menuService from '@/services/menu'

const loading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑角色' : '新增角色')
const formRef = ref(null)
const form = reactive({
  id: null,
  code: '',
  name: '',
  status: 1
})

const formRules = {
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const menuDialogVisible = ref(false)
const currentRole = ref(null)
const menuTree = ref([])
const checkedMenuIds = ref([])
const menuTreeRef = ref(null)

const treeProps = {
  children: 'children',
  label: 'name'
}

// 获取角色列表
const getRoleList = async () => {
  loading.value = true
  try {
    const res = await roleService.getRoleList({ page: 1, size: 100 })
    if (res.code === 200) {
      tableData.value = res.data.list || []
    }
  } catch (error) {
    ElMessage.error(error.message || '获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 新增角色
const handleAdd = () => {
  form.id = null
  form.code = ''
  form.name = ''
  form.status = 1
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row) => {
  form.id = row.id
  form.code = row.code
  form.name = row.name
  form.status = row.status
  dialogVisible.value = true
}

// 删除角色
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await roleService.deleteRole(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getRoleList()
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
        const api = form.id ? roleService.updateRole : roleService.addRole
        const res = await api(form)
        if (res.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          getRoleList()
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

// 菜单权限配置
const handleAssignMenus = async (row) => {
  currentRole.value = row
  menuDialogVisible.value = true
  checkedMenuIds.value = []

  // 获取菜单树
  try {
    const res = await menuService.getMenuTree()
    if (res.code === 200) {
      menuTree.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取菜单列表失败')
  }

  // 获取角色当前菜单
  try {
    const res = await menuService.getRoleMenus(row.id)
    if (res.code === 200) {
      checkedMenuIds.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取角色菜单失败')
  }
}

// 提交菜单配置
const handleMenuSubmit = async () => {
  const keys = menuTreeRef.value?.getCheckedKeys() || []
  try {
    const res = await menuService.assignRoleMenus(currentRole.value.id, keys)
    if (res.code === 200) {
      ElMessage.success('菜单权限配置成功')
      menuDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '配置失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '配置失败')
  }
}

// 菜单弹窗关闭
const handleMenuDialogClose = () => {
  currentRole.value = null
  menuTree.value = []
  checkedMenuIds.value = []
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
  getRoleList()
})
</script>

<style scoped>
.role-management {
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
</style>
