<template>
  <div class="menu-management">
    <div class="page-header">
      <h2 class="page-title">菜单管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增菜单</el-button>
    </div>

    <el-card>
      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children' }"
        default-expand-all
      >
        <el-table-column prop="name" label="菜单名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限标识" min-width="180">
          <template #default="{ row }">
            <el-tag v-if="row.permissionCode" size="small">{{ row.permissionCode }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="180">
          <template #default="{ row }">
            <span>{{ row.path || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="{ row }">
            <el-icon v-if="row.icon">
              <component :is="row.icon" />
            </el-icon>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" :icon="Plus" @click="handleAddChild(row)">新增子菜单</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑菜单弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单（不选则为顶级菜单）"
            clearable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="请输入权限标识（如：user:add）" />
        </el-form-item>
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由路径（如：/user）" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <div class="icon-input-wrapper">
            <el-input
              v-model="form.icon"
              placeholder="请选择或输入图标名称"
              @click="showIconPicker = true"
            >
              <template #prefix>
                <el-icon v-if="form.icon" :is="form.icon" />
                <span v-else>-</span>
              </template>
            </el-input>
            <el-button
              type="primary"
              link
              @click="showIconPicker = true"
              style="margin-left: 8px;"
            >
              选择图标
            </el-button>
          </div>
        </el-form-item>

        <!-- 图标选择弹窗 -->
        <el-dialog
          v-model="showIconPicker"
          title="选择图标"
          width="800px"
          @close="handleIconPickerClose"
        >
          <div class="icon-search">
            <el-input
              v-model="iconSearch"
              placeholder="搜索图标"
              clearable
              @input="filterIcons"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>

          <div class="icon-grid">
            <div
              v-for="icon in filteredIcons"
              :key="icon.name"
              class="icon-item"
              :class="{ 'active': form.icon === icon.name }"
              @click="selectIcon(icon.name)"
            >
              <el-icon :size="24">
                <component :is="icon.name" />
              </el-icon>
              <span class="icon-name">{{ icon.name }}</span>
            </div>
          </div>

          <template #footer>
            <el-button @click="showIconPicker = false">取消</el-button>
          </template>
        </el-dialog>
        <el-form-item label="排序" prop="orderNo">
          <el-input-number v-model="form.orderNo" :min="0" :max="9999" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { Plus, Edit, Delete, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import menuService from '@/services/menu'

const loading = ref(false)
const tableData = ref([])
const menuTreeOptions = ref([])

const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑菜单' : '新增菜单')
const formRef = ref(null)
const form = reactive({
  id: null,
  parentId: null,
  name: '',
  permissionCode: '',
  path: '',
  icon: '',
  orderNo: 0,
  status: 1
})

// 图标选择相关
const showIconPicker = ref(false)
const iconSearch = ref('')
const allIcons = ref([])
const filteredIcons = ref([])

const formRules = {
  name: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  orderNo: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取菜单列表
const getMenuList = async () => {
  loading.value = true
  try {
    const res = await menuService.getMenuList()
    if (res.code === 200) {
      const menus = res.data || []
      // 构建树形结构
      tableData.value = buildMenuTree(menus)
      // 构建下拉选项（排除当前编辑节点及其子节点）
      menuTreeOptions.value = buildMenuTreeOptions(menus)
    }
  } catch (error) {
    ElMessage.error(error.message || '获取菜单列表失败')
  } finally {
    loading.value = false
  }
}

// 构建菜单树
const buildMenuTree = (menus, parentId = null) => {
  const result = []
  for (const menu of menus) {
    if (menu.parentId === parentId) {
      const children = buildMenuTree(menus, menu.id)
      const node = {
        ...menu,
        children: children.length > 0 ? children : undefined
      }
      result.push(node)
    }
  }
  // 按排序排序
  return result.sort((a, b) => a.orderNo - b.orderNo)
}

// 构建菜单树选项（用于上级菜单选择）
const buildMenuTreeOptions = (menus, parentId = null) => {
  const result = []
  for (const menu of menus) {
    if (menu.parentId === parentId) {
      const children = buildMenuTreeOptions(menus, menu.id)
      const node = {
        id: menu.id,
        name: menu.name,
        children: children.length > 0 ? children : undefined
      }
      result.push(node)
    }
  }
  return result.sort((a, b) => a.orderNo - b.orderNo)
}

// 新增菜单
const handleAdd = () => {
  form.id = null
  form.parentId = null
  form.name = ''
  form.permissionCode = ''
  form.path = ''
  form.icon = ''
  form.orderNo = 0
  form.status = 1
  dialogVisible.value = true
}

// 新增子菜单
const handleAddChild = (row) => {
  form.id = null
  form.parentId = row.id
  form.name = ''
  form.permissionCode = ''
  form.path = ''
  form.icon = ''
  form.orderNo = 0
  form.status = 1
  dialogVisible.value = true
}

// 编辑菜单
const handleEdit = (row) => {
  form.id = row.id
  form.parentId = row.parentId || null
  form.name = row.name
  form.permissionCode = row.permissionCode || ''
  form.path = row.path || ''
  form.icon = row.icon || ''
  form.orderNo = row.orderNo
  form.status = row.status
  dialogVisible.value = true
}

// 删除菜单
const handleDelete = (row) => {
  // 检查是否有子菜单
  const hasChildren = (parentId) => {
    return tableData.value.some(menu => {
      if (menu.parentId === parentId) return true
      if (menu.children) {
        return menu.children.some(child => child.parentId === parentId)
      }
      return false
    })
  }

  if (hasChildren(row.id)) {
    ElMessage.warning('请先删除子菜单')
    return
  }

  ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await menuService.deleteMenu(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getMenuList()
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
        const data = { ...form }
        // 处理 parentId，如果为 null 或 undefined 则设置为 0
        if (!data.parentId) {
          data.parentId = 0
        }

        const api = form.id ? menuService.updateMenu : menuService.addMenu
        const res = await api(data)
        if (res.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          getMenuList()
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

// 图标选择相关方法
const initIcons = () => {
  // 获取所有 Element Plus 图标
  const icons = []
  for (const key in ElementPlusIconsVue) {
    if (ElementPlusIconsVue[key].name) {
      icons.push(ElementPlusIconsVue[key])
    }
  }
  allIcons.value = icons
  filteredIcons.value = icons
}

const filterIcons = () => {
  if (!iconSearch.value) {
    filteredIcons.value = allIcons.value
  } else {
    const lowerSearch = iconSearch.value.toLowerCase()
    filteredIcons.value = allIcons.value.filter(icon =>
      icon.name.toLowerCase().includes(lowerSearch)
    )
  }
}

const selectIcon = (iconName) => {
  form.icon = iconName
  showIconPicker.value = false
}

const handleIconPickerClose = () => {
  iconSearch.value = ''
  filterIcons()
}

onMounted(() => {
  getMenuList()
  initIcons()
})
</script>

<style scoped>
.menu-management {
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

.icon-input-wrapper {
  display: flex;
  align-items: center;
}

.icon-search {
  margin-bottom: 16px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  padding: 12px;
  border-radius: 4px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.icon-item:hover {
  background-color: #f5f7fa;
  border-color: #409eff;
}

.icon-item.active {
  background-color: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

.icon-name {
  margin-top: 8px;
  font-size: 12px;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100px;
}
</style>
