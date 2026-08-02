<template>
  <div class="logo-container">
    <el-icon class="logo-icon"><Management /></el-icon>
    <span v-if="!isCollapse" class="logo-text">怀真管理系统</span>
  </div>
  <el-menu
    :default-active="activeMenu"
    :collapse="isCollapse"
    :collapse-transition="false"
    class="sidebar-menu"
    router
  >
    <MenuItem
      v-for="menu in menuTree"
      :key="menu.id"
      :menu="menu"
    />
  </el-menu>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import menuService from '@/services/menu'
import { Management } from '@element-plus/icons-vue'
import MenuItem from './MenuItem.vue'

interface Props {
  isCollapse: boolean
}

defineProps<Props>()

const route = useRoute()

const activeMenu = computed(() => route.path)
const menuTree = ref([])

// 加载用户菜单
const loadUserMenus = async () => {
  try {
    const result = await menuService.getUserMenuTree()
    if (result && result.code === 200 && result.data) {
      menuTree.value = result.data
    }
  } catch (error) {
    console.error('加载用户菜单失败:', error)
  }
}

onMounted(() => {
  loadUserMenus()
})
</script>

<style scoped>
.logo-container {
  height: 56px;
  background-color: var(--app-sidebar-bg);
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid var(--app-border);
  overflow: hidden;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.logo-icon {
  font-size: 24px;
  color: var(--app-primary);
  flex-shrink: 0;
}

.logo-text {
  margin-left: 12px;
  font-size: 17px;
  font-weight: 600;
  color: var(--app-text);
  white-space: nowrap;
  letter-spacing: 0.02em;
}

.sidebar-menu {
  height: calc(100% - 56px);
  border-right: none;
  background-color: var(--app-sidebar-bg);
  transition: background-color 0.3s ease;
  overflow-y: auto;
  overflow-x: hidden;
  /* 隐藏滚动条但保留滚轮滚动(菜单过长时仍可滚动) */
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.sidebar-menu::-webkit-scrollbar {
  width: 0;
  display: none;
}

/* 兼容:若菜单内部使用 el-scrollbar,同样隐藏其滚动条 */
.sidebar-menu :deep(.el-scrollbar__bar) {
  display: none;
}

/* 菜单项圆角与间距 */
.sidebar-menu :deep(.el-menu-item) {
  margin: 2px 8px;
  border-radius: var(--app-radius-sm);
  height: 44px;
  line-height: 44px;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  margin: 2px 8px;
  border-radius: var(--app-radius-sm);
  height: 44px;
  line-height: 44px;
}

/* 激活态:teal tint 背景 + 主色文字 */
.sidebar-menu :deep(.el-menu-item.is-active) {
  color: var(--app-primary);
  background-color: var(--app-primary-soft);
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: var(--app-border-light);
}

/* 折叠态去掉外边距,居中显示 */
.sidebar-menu.el-menu--collapse :deep(.el-menu-item),
.sidebar-menu.el-menu--collapse :deep(.el-sub-menu__title) {
  margin: 2px 4px;
}
</style>
