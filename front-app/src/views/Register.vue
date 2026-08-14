<template>
  <div class="main-col">
    <div class="box" style="max-width:420px;margin:40px auto;">
      <div class="box-header">注册怀真</div>
      <div class="box-body">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" class="form-control" placeholder="用户名" />
        </div>
        <div class="form-group">
          <label>昵称</label>
          <input v-model="form.nickname" class="form-control" placeholder="昵称（选填）" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" class="form-control" placeholder="密码" />
        </div>
        <button class="btn btn-primary" style="width:100%;text-align:center;" :disabled="submitting" @click="submit">
          {{ submitting ? '注册中...' : '注册' }}
        </button>
        <div style="margin-top:14px;text-align:center;font-size:13px;">
          已有账号？<router-link to="/login">去登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { useUser } from '@/store/user'
import { toast } from '@/utils/toast'

const router = useRouter()
const { setUser } = useUser()
const submitting = ref(false)
const form = ref({ username: '', nickname: '', password: '' })

async function submit() {
  if (!form.value.username || !form.value.password) {
    toast('请输入用户名和密码')
    return
  }
  submitting.value = true
  try {
    const data = await register(form.value)
    setUser(data)
    toast('注册成功')
    router.push('/')
  } catch (e) {
    toast(e.message)
  } finally {
    submitting.value = false
  }
}
</script>
