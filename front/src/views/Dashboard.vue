<template>
  <div class="dashboard">
    <div class="page-header">
      <h2 class="page-title">仪表盘</h2>
      <p class="page-subtitle">系统概览</p>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon primary">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">1,234</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><DocumentChecked /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">567</div>
              <div class="stat-label">今日访问</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">89</div>
              <div class="stat-label">待处理事项</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon danger">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">3</div>
              <div class="stat-label">系统告警</div>
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
              <span>最近登录</span>
            </div>
          </template>
          <el-table :data="loginData" style="width: 100%">
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="ip" label="IP地址" width="140" />
            <el-table-column prop="time" label="登录时间" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === '成功' ? 'success' : 'danger'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统通知</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in notifications"
              :key="index"
              :type="item.type"
              :timestamp="item.time"
            >
              {{ item.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { User, DocumentChecked, Timer, Warning } from '@element-plus/icons-vue'

interface LoginRecord {
  username: string
  ip: string
  time: string
  status: string
}

interface Notification {
  content: string
  time: string
  type: 'primary' | 'success' | 'warning' | 'danger'
}

const loginData: LoginRecord[] = [
  { username: 'admin', ip: '192.168.1.100', time: '2024-01-15 10:30:00', status: '成功' },
  { username: 'user01', ip: '192.168.1.101', time: '2024-01-15 10:28:00', status: '成功' },
  { username: 'user02', ip: '192.168.1.102', time: '2024-01-15 10:25:00', status: '失败' },
  { username: 'user03', ip: '192.168.1.103', time: '2024-01-15 10:20:00', status: '成功' },
]

const notifications: Notification[] = [
  { content: '系统升级成功，版本 v2.1.0', time: '2024-01-15 09:00', type: 'success' },
  { content: '发现异常登录，请及时检查', time: '2024-01-14 18:30', type: 'warning' },
  { content: '数据库备份已完成', time: '2024-01-14 02:00', type: 'primary' },
  { content: '服务器CPU使用率超过90%', time: '2024-01-13 14:20', type: 'danger' },
]
</script>

<style scoped>
.dashboard {
  height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--app-text);
  margin: 0 0 6px 0;
}

.page-subtitle {
  font-size: 14px;
  color: var(--app-text-secondary);
  margin: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 0;
  border-radius: var(--app-radius-lg);
  border: 1px solid var(--app-border);
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: var(--app-radius);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.stat-icon.primary {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.stat-icon.success {
  background-color: var(--el-color-success-light-9);
  color: var(--el-color-success);
}

.stat-icon.warning {
  background-color: var(--el-color-warning-light-9);
  color: var(--el-color-warning);
}

.stat-icon.danger {
  background-color: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--app-text);
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: var(--app-text-secondary);
  margin-top: 6px;
}

.content-row {
  margin-top: 20px;
}

.card-header {
  font-size: 15px;
  font-weight: 600;
  color: var(--app-text);
}

:deep(.el-card) {
  border-radius: var(--app-radius-lg);
  border-color: var(--app-border);
}
</style>
