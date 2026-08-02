<template>
  <div class="header-content">
    <div class="header-left">
      <el-icon
        class="collapse-icon"
        @click="$emit('toggleCollapse')"
      >
        <Fold v-if="!isCollapse" />
        <Expand v-else />
      </el-icon>
      <span class="system-name">怀真 · 管理系统</span>
    </div>

    <div class="header-right">
      <el-icon class="header-action" @click="toggleTheme">
        <Moon v-if="theme === 'light'" />
        <Sunny v-else />
      </el-icon>
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-icon class="user-icon"><UserFilled /></el-icon>
          <span class="username">{{ username || '管理员' }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人信息
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Fold, Expand, UserFilled, User, SwitchButton, Moon, Sunny } from '@element-plus/icons-vue'
import auth from '../services/auth'
import { useTheme } from '../composables/useTheme'

interface Props {
  isCollapse: boolean
}

const props = defineProps<Props>()

defineEmits<{
  toggleCollapse: []
}>()

const { theme, toggleTheme } = useTheme()

const router = useRouter()
const username = ref('')

const handleCommand = async (command: string) => {
  if (command === 'profile') {
    // 跳转到个人信息页面
    router.push('/profile')
  } else if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      // 调用登出接口
      await auth.logout()

      // 清除本地 token
      auth.removeToken()

      ElMessage.success('退出成功')

      // 跳转到登录页
      router.push('/login')
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('退出失败')
      }
    }
  }
}

onMounted(() => {
  // 从 JWT 中解析用户名
  username.value = auth.getUsername() || '管理员'
})
</script>

<style scoped>
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  color: var(--app-text-secondary);
  transition: color 0.2s;
}

.collapse-icon:hover {
  color: var(--app-text);
}

.system-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--app-text);
  letter-spacing: 0.02em;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-action {
  font-size: 20px;
  cursor: pointer;
  color: var(--app-text-secondary);
  transition: color 0.2s;
}

.header-action:hover {
  color: var(--app-primary);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--app-text-secondary);
  transition: color 0.2s;
  outline: none;
}

.user-info:hover {
  color: var(--app-text);
}

.user-icon {
  font-size: 20px;
}

.username {
  font-size: 14px;
}
</style>
