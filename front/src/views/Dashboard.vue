<template>
  <div class="dashboard">
    <div class="page-header">
      <h2 class="page-title">仪表盘</h2>
      <p class="page-subtitle">论坛数据概览</p>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon primary">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalPosts || 0 }}</div>
              <div class="stat-label">总帖子数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning">
              <el-icon><FolderOpened /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalForums || 0 }}</div>
              <div class="stat-label">版块数量</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon danger">
              <el-icon><DocumentCopy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activePosts || 0 }}</div>
              <div class="stat-label">正常帖子</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新帖子</span>
            </div>
          </template>
          <el-table :data="recentPosts" style="width: 100%" v-loading="postsLoading">
            <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
            <el-table-column prop="authorName" label="作者" width="100" />
            <el-table-column prop="forumName" label="版块" width="100" />
            <el-table-column prop="createdAt" label="发布时间" width="160">
              <template #default="{ row }">
                {{ formatDateTime(row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>活跃用户</span>
            </div>
          </template>
          <el-table :data="activeUsers" style="width: 100%" v-loading="usersLoading">
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="昵称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="role" label="角色" width="80">
              <template #default="{ row }">
                <el-tag :type="row.role === 1 ? 'warning' : 'info'" size="small">
                  {{ row.role === 1 ? '管理员' : '用户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '正常' : '已封禁' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, User, FolderOpened, DocumentCopy } from '@element-plus/icons-vue'
import forumPostService from '@/services/forumPost'
import forumUserService from '@/services/forumUser'
import forumBoardService from '@/services/forumBoard'

const stats = reactive({
  totalPosts: 0,
  totalUsers: 0,
  totalForums: 0,
  activePosts: 0
})

const recentPosts = ref([])
const activeUsers = ref([])
const postsLoading = ref(false)
const usersLoading = ref(false)

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const loadStats = async () => {
  try {
    const [postsRes, usersRes, forumsRes] = await Promise.all([
      forumPostService.getPostStats(),
      forumUserService.getUserStats(),
      forumBoardService.getBoardStats()
    ])

    if (postsRes.code === 200) {
      stats.totalPosts = postsRes.data.totalPosts || 0
      stats.activePosts = postsRes.data.activePosts || 0
    }

    if (usersRes.code === 200) {
      stats.totalUsers = usersRes.data.totalUsers || 0
    }

    if (forumsRes.code === 200) {
      stats.totalForums = forumsRes.data.totalForums || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadRecentPosts = async () => {
  postsLoading.value = true
  try {
    const res = await forumPostService.getPostList({
      page: 1,
      size: 5,
      status: 1  // 只显示正常状态的帖子
    })
    if (res.code === 200) {
      recentPosts.value = res.data.list || []
    }
  } catch (error) {
    ElMessage.error('加载最新帖子失败')
  } finally {
    postsLoading.value = false
  }
}

const loadActiveUsers = async () => {
  usersLoading.value = true
  try {
    const res = await forumUserService.getUserList({
      page: 1,
      size: 5,
      status: 1  // 只显示正常状态的用户
    })
    if (res.code === 200) {
      activeUsers.value = res.data.list || []
    }
  } catch (error) {
    ElMessage.error('加载活跃用户失败')
  } finally {
    usersLoading.value = false
  }
}

onMounted(() => {
  loadStats()
  loadRecentPosts()
  loadActiveUsers()
})
</script>

<style scoped>
.dashboard {
  height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 500;
  color: #111827;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 0;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.primary {
  background-color: #dbeafe;
  color: #3b82f6;
}

.stat-icon.success {
  background-color: #d1fae5;
  color: #10b981;
}

.stat-icon.warning {
  background-color: #fef3c7;
  color: #f59e0b;
}

.stat-icon.danger {
  background-color: #fee2e2;
  color: #ef4444;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #111827;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

.content-row {
  margin-top: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 500;
  color: #111827;
}
</style>
