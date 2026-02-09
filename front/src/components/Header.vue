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
      <span class="system-name">Admin System</span>
    </div>

    <div class="header-right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-icon class="user-icon"><UserFilled /></el-icon>
          <span class="username">{{ username || '管理员' }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>
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
import { Fold, Expand, UserFilled, User, SwitchButton } from '@element-plus/icons-vue'
import auth from '../services/auth'

interface Props {
  isCollapse: boolean
}

const props = defineProps<Props>()

defineEmits<{
  toggleCollapse: []
}>()

const router = useRouter()
const username = ref('')

const handleCommand = async (command: string) => {
  if (command === 'logout') {
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
  // 这里可以从 JWT 中解析用户名
  username.value = '管理员'
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
  color: #6b7280;
  transition: color 0.3s;
}

.collapse-icon:hover {
  color: #111827;
}

.system-name {
  font-size: 18px;
  font-weight: 500;
  color: #111827;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #374151;
  transition: color 0.3s;
}

.user-info:hover {
  color: #111827;
}

.user-icon {
  font-size: 20px;
}

.username {
  font-size: 14px;
}
</style>
