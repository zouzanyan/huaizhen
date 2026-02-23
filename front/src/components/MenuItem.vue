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
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps({
  menu: {
    type: Object,
    required: true
  }
})

const icon = computed(() => {
  // 使用动态图标名称从 Element Plus 图标库中获取图标组件
  const iconName = props.menu.icon

  if (!iconName) {
    // 默认图标
    return ElementPlusIconsVue.Management
  }

  // 从 ElementPlusIconsVue 中获取对应名称的图标
  const iconComponent = ElementPlusIconsVue[iconName]

  // 如果找到图标则返回，否则返回默认图标
  return iconComponent || ElementPlusIconsVue.Management
})
</script>
