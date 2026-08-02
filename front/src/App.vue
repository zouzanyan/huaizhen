<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  width: 100%;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body {
  background-color: var(--app-bg);
  color: var(--app-text);
  /* 主题切换时的平滑过渡 */
  transition: background-color 0.3s ease, color 0.3s ease;
}

#app {
  height: 100%;
  width: 100%;
}

/* 自定义滚动条,贴合主题 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: var(--app-border);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--app-text-tertiary);
}
</style>
