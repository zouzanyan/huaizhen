<template>
  <nav class="navbar">
    <div class="navbar-inner">
      <router-link to="/" class="logo">怀真</router-link>
      <div class="nav-links">
        <router-link to="/">技术</router-link>
        <router-link to="/">创意</router-link>
        <router-link to="/">好玩</router-link>
        <router-link to="/">R2</router-link>
      </div>
      <div class="nav-search">
        <input v-model="keyword" placeholder="搜索 怀真" @keyup.enter="search" />
      </div>
      <div class="nav-right">
        <router-link v-if="!isLogin()" to="/login" class="btn btn-sm">登录</router-link>
        <router-link v-if="!isLogin()" to="/register" class="btn btn-sm">注册</router-link>
        <router-link v-if="isLogin()" to="/new" class="btn btn-primary btn-sm">发帖</router-link>
        <template v-if="isLogin()">
          <router-link to="/" class="user-name">{{ state.user.nickname }}</router-link>
          <a href="javascript:void(0)" class="btn btn-sm" @click="handleLogout">退出</a>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUser } from '@/store/user'

const router = useRouter()
const { state, isLogin, logout } = useUser()
const keyword = ref('')

function search() {
  router.push({ name: 'home', query: keyword.value ? { keyword: keyword.value } : {} })
}

function handleLogout() {
  logout()
  router.push('/')
}
</script>
