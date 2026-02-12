<template>
  <div class="profile-page">
    <div class="page-header">
      <h2 class="page-title">个人信息</h2>
    </div>

    <el-card>
      <div class="profile-content">
        <div class="profile-avatar">
          <el-avatar
            :size="100"
            :src="avatar"
            class="avatar"
          >
            <el-icon><User /></el-icon>
          </el-avatar>
        </div>

        <div class="profile-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户名">
              <span class="info-value">{{ userInfo.username }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="用户ID">
              <span class="info-value">{{ userInfo.id }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
                {{ userInfo.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              <span class="info-value">{{ formatDateTime(userInfo.createdAt) }}</span>
            </el-descriptions-item>
          </el-descriptions>

          <div class="roles-section">
            <h3>用户角色</h3>
            <template v-if="roles.length > 0">
              <el-tag v-for="role in roles" :key="role.id" class="role-tag">
                {{ role.name }}
              </el-tag>
            </template>
            <span v-else class="no-roles">暂无角色</span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const userInfo = ref({})
const roles = ref([])
const avatar = ref('')

// 获取用户信息
const loadUserProfile = async () => {
  try {
    const res = await request({
      url: '/api/sys/user/profile',
      method: 'get'
    })
    if (res.code === 200) {
      userInfo.value = res.data || {}
      // 根据用户名生成头像（示例）
      avatar.value = `https://ui-avatars.com/api/?name=${encodeURIComponent(userInfo.value.username)}&background=3B82F6&color=fff`
    } else {
      ElMessage.error(res.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 获取用户角色
const loadUserRoles = async () => {
  try {
    const res = await request({
      url: '/api/sys/user/profile/roles',
      method: 'get'
    })
    if (res.code === 200) {
      roles.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取用户角色失败')
    }
  } catch (error) {
    console.error('获取用户角色失败:', error)
  }
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  loadUserProfile()
  loadUserRoles()
})
</script>

<style scoped>
.profile-page {
  height: 100%;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 500;
  color: #111827;
  margin: 0;
}

.profile-content {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.profile-avatar {
  text-align: center;
}

.avatar {
  margin-bottom: 16px;
}

.profile-info {
  flex: 1;
}

.info-value {
  font-weight: 500;
}

.roles-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
}

.roles-section h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #374151;
}

.role-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.no-roles {
  color: #9ca3af;
  font-style: italic;
}
</style>