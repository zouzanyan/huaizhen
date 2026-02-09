<template>
  <!-- 有子菜单 -->
  <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.id.toString()">
    <template #title>
      <el-icon><component :is="icon" /></el-icon>
      <span>{{ menu.name }}</span>
    </template>
    <template v-for="child in menu.children" :key="child.id">
      <MenuItem :menu="child" />
    </template>
  </el-sub-menu>
  <!-- 没有子菜单 -->
  <el-menu-item v-else :index="menu.path">
    <el-icon><component :is="icon" /></el-icon>
    <template #title>{{ menu.name }}</template>
  </el-menu-item>
</template>

<script setup>
import { computed } from 'vue'
import {
  Odometer,
  Management,
  Setting,
  User,
  Avatar,
  Document,
  Monitor,
  Edit
} from '@element-plus/icons-vue'

const props = defineProps({
  menu: {
    type: Object,
    required: true
  }
})

// 图标映射
const iconMap = {
  Odometer,
  Management,
  Setting,
  User,
  Avatar,
  Document,
  Monitor,
  Edit
}

const icon = computed(() => {
  return iconMap[props.menu.icon] || Management
})
</script>
