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
  background-color: var(--app-sidebar-bg);
  border-right: 1px solid var(--app-border);
  transition: width 0.3s ease, background-color 0.3s ease, border-color 0.3s ease;
  overflow: hidden;
}

.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: var(--app-bg);
  transition: background-color 0.3s ease;
}

.layout-header {
  background-color: var(--app-card-bg);
  border-bottom: 1px solid var(--app-border);
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.layout-main {
  background-color: var(--app-bg);
  padding: 24px;
  overflow-y: auto;
  transition: background-color 0.3s ease;
}

.content-wrapper {
  min-height: calc(100vh - 104px);
}
</style>
