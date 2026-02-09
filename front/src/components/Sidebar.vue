<template>
  <div class="logo-container">
    <el-icon class="logo-icon"><Management /></el-icon>
    <span v-if="!isCollapse" class="logo-text">管理系统</span>
  </div>
  <el-menu
    :default-active="activeMenu"
    :collapse="isCollapse"
    :collapse-transition="false"
    class="sidebar-menu"
    background-color="#004E8C"
    text-color="#ffffff"
    active-text-color="#ffffff"
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
  background-color: #003A6E;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #0C4A8A;
  overflow: hidden;
}

.logo-icon {
  font-size: 24px;
  color: #ffffff;
  flex-shrink: 0;
}

.logo-text {
  margin-left: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  white-space: nowrap;
}

.sidebar-menu {
  height: calc(100% - 56px);
  border-right: none;
}
</style>
