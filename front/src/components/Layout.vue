<template>
  <el-container class="admin-layout">
    <el-aside
      :width="isCollapse ? '64px' : '200px'"
      class="sidebar-aside"
    >
      <Sidebar :is-collapse="isCollapse" />
    </el-aside>

    <el-container class="main-container">
      <el-header height="56px" class="layout-header">
        <Header
          :is-collapse="isCollapse"
          @toggle-collapse="toggleCollapse"
        />
      </el-header>

      <el-main class="layout-main">
        <div class="content-wrapper">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const isCollapse = ref(false)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  width: 100vw;
}

.sidebar-aside {
  background-color: #004E8C;
  transition: width 0.3s;
  overflow-x: hidden;
}

.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.layout-header {
  background-color: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.layout-main {
  background-color: #F0F6FF;
  padding: 20px;
  overflow-y: auto;
}

.content-wrapper {
  background-color: #ffffff;
  border-radius: 4px;
  padding: 20px;
  min-height: calc(100vh - 96px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}
</style>
