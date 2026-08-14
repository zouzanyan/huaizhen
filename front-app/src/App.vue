<template>
  <div id="app">
    <NavBar />
    <main class="container">
      <router-view />
    </main>
    <footer class="site-footer">
      <span>怀真社区 · Powered by qinfeng</span>
    </footer>
  </div>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue'
</script>
