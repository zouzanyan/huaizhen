<template>
  <div class="main-col">
    <div class="box" style="max-width:420px;margin:40px auto;">
      <div class="box-header">登录怀真</div>
      <div class="box-body">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" class="form-control" placeholder="用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" class="form-control" placeholder="密码" />
        </div>
        <button class="btn btn-primary" style="width:100%;text-align:center;" :disabled="submitting" @click="submit">
          {{ submitting ? '登录中...' : '登录' }}
        </button>
        <div style="margin-top:14px;text-align:center;font-size:13px;">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { useUser } from '@/store/user'
import { toast } from '@/utils/toast'

const route = useRoute()
const router = useRouter()
const { setUser } = useUser()
const submitting = ref(false)
const form = ref({ username: '', password: '' })

async function submit() {
  if (!form.value.username || !form.value.password) {
    toast('请输入用户名和密码')
    return
  }
  submitting.value = true
  try {
    const data = await login(form.value)
    setUser(data)
    toast('登录成功')
    router.push(route.query.redirect || '/')
  } catch (e) {
    toast(e.message)
  } finally {
    submitting.value = false
  }
}
</script>
