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
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #3d7a9e 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.05) 0%, transparent 50%),
    linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.02) 50%, transparent 70%);
  background-size: 100% 100%, 100% 100%, 200% 200%;
  animation: backgroundMove 20s ease-in-out infinite;
}

@keyframes backgroundMove {
  0%, 100% {
    background-position: 0 0, 0 0, 0 0;
  }
  50% {
    background-position: 0 0, 0 0, 50% 50%;
  }
}

.login-card {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 12px;
  box-shadow:
    0 10px 40px rgba(0, 0, 0, 0.2),
    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
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
  padding: 14px 16px;
  border: 1.5px solid #e0e6ed;
  border-radius: 8px;
  font-size: 15px;
  box-sizing: border-box;
  transition: all 0.3s ease;
  background: #f8f9fb;
  color: #2c3e50;
}

.form-input:focus {
  outline: none;
  border-color: #2a5298;
  background: white;
  box-shadow: 0 0 0 3px rgba(42, 82, 152, 0.1);
}

.login-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(30, 60, 114, 0.3);
}

.login-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #2a5298 0%, #3d7a9e 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(30, 60, 114, 0.4);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  background: #a0cfff;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}
</style>