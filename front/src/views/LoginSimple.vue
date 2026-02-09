<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>系统登录</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="username">用户名：</label>
          <input
            id="username"
            v-model="formData.username"
            type="text"
            placeholder="请输入用户名"
            class="form-input"
          />
        </div>
        <div class="form-group">
          <label for="password">密码：</label>
          <input
            id="password"
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            class="form-input"
          />
        </div>
        <button type="submit" class="login-button" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import auth from '@/services/auth'

const router = useRouter()
const formData = ref({
  username: '',
  password: ''
})
const loading = ref(false)

const handleSubmit = async (e) => {
  e.preventDefault()

  if (!formData.value.username || !formData.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }

  try {
    loading.value = true
    const response = await auth.login(formData.value.username, formData.value.password)

    console.log('登录响应:', response)

    // 存储 token
    if (response && response.data && response.data.token) {
      auth.setToken(response.data.token)
      console.log('Token 已保存:', response.data.token)
      ElMessage.success('登录成功')

      // 跳转到首页
      await router.push('/')
    } else {
      console.error('响应中没有 token:', response)
      ElMessage.error('登录失败：服务器响应异常')
    }
  } catch (error) {
    console.error('登录错误:', error)

    if (error.response) {
      ElMessage.error(error.response.data?.message || '登录失败')
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('网络错误，请重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #409eff;
}

.login-button {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.login-button:hover:not(:disabled) {
  background: #66b1ff;
}

.login-button:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}
</style>