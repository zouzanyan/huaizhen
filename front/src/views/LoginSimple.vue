<template>
  <div class="login-container">
    <div class="login-glow"></div>

    <!-- 角落主题切换 -->
    <el-icon class="theme-toggle" @click="toggleTheme">
      <Moon v-if="theme === 'light'" />
      <Sunny v-else />
    </el-icon>

    <el-card class="login-card" shadow="never">
      <div class="brand">
        <div class="brand-icon">
          <el-icon><Management /></el-icon>
        </div>
        <h1 class="brand-title">怀真</h1>
        <p class="brand-subtitle">管理系统</p>
      </div>

      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form" label-position="top">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Management, Moon, Sunny } from '@element-plus/icons-vue'
import auth from '@/services/auth'
import { useTheme } from '@/composables/useTheme'

const { theme, toggleTheme } = useTheme()

const router = useRouter()
const loginForm = ref({
  username: '',
  password: ''
})
const loading = ref(false)
const loginFormRef = ref(null)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true

    const response = await auth.login(loginForm.value.username, loginForm.value.password)

    if (response && response.token) {
      auth.setToken(response.token)
      ElMessage.success('登录成功')
      await router.push('/')
    } else {
      ElMessage.error('登录失败:服务器响应异常')
    }
  } catch (error) {
    // 表单校验失败时 error 为 false,不提示网络错误
    if (error && error.response) {
      ElMessage.error(error.response.data?.message || '登录失败')
    } else if (error && error.message) {
      ElMessage.error(error.message)
    } else if (error) {
      ElMessage.error('网络错误,请重试')
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
  background-color: var(--app-bg);
  position: relative;
  overflow: hidden;
  transition: background-color 0.3s ease;
}

/* 背景柔和的 teal 光晕(主题自适应) */
.login-glow {
  position: absolute;
  top: -20%;
  left: 50%;
  transform: translateX(-50%);
  width: 800px;
  height: 600px;
  background: radial-gradient(ellipse at center, var(--app-primary-soft) 0%, transparent 65%);
  opacity: 0.8;
  pointer-events: none;
  transition: opacity 0.3s ease;
}

.theme-toggle {
  position: absolute;
  top: 24px;
  right: 24px;
  font-size: 22px;
  cursor: pointer;
  color: var(--app-text-secondary);
  z-index: 10;
  transition: color 0.2s;
}

.theme-toggle:hover {
  color: var(--app-primary);
}

.login-card {
  width: 400px;
  padding: 16px 8px;
  border-radius: var(--app-radius-lg);
  border: 1px solid var(--app-border);
  background-color: var(--app-card-bg);
  box-shadow: var(--app-shadow-lg);
  position: relative;
  z-index: 1;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

:deep(.login-card .el-card__body) {
  padding: 32px;
}

.brand {
  text-align: center;
  margin-bottom: 32px;
}

.brand-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
  border-radius: var(--app-radius-lg);
  background: linear-gradient(135deg, var(--app-primary) 0%, var(--app-primary-active) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.brand-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--app-text);
  margin: 0 0 4px 0;
  letter-spacing: 0.04em;
}

.brand-subtitle {
  font-size: 14px;
  color: var(--app-text-tertiary);
  margin: 0;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: var(--app-radius);
}

.login-button {
  width: 100%;
  border-radius: var(--app-radius);
  font-weight: 500;
}
</style>
