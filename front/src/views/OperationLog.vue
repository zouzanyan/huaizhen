<template>
  <div class="operation-log">
    <div class="page-header">
      <h2 class="page-title">操作日志</h2>
    </div>

    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入关键词"
          clearable
          style="width: 200px"
        />
        <el-select
          v-model="searchForm.type"
          placeholder="操作类型"
          clearable
          style="width: 140px"
        >
          <el-option label="登录" value="login" />
          <el-option label="创建" value="create" />
          <el-option label="更新" value="update" />
          <el-option label="删除" value="delete" />
          <el-option label="导出" value="export" />
        </el-select>
        <el-date-picker
          v-model="searchForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 280px"
        />
        <el-button type="primary" :icon="Search">查询</el-button>
        <el-button :icon="Refresh">重置</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="type" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.type)" size="small">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="操作模块" width="120" />
        <el-table-column prop="description" label="操作描述" min-width="200" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '成功' ? 'success' : 'danger'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="160" />
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'

interface OperationLog {
  id: number
  username: string
  type: string
  module: string
  description: string
  ip: string
  status: string
  createTime: string
}

const searchForm = reactive({
  keyword: '',
  type: '',
  dateRange: null as Date[] | null
})

const tableData: OperationLog[] = [
  { id: 1, username: 'admin', type: 'login', module: '登录', description: '用户登录成功', ip: '192.168.1.100', status: '成功', createTime: '2024-01-15 10:30:00' },
  { id: 2, username: 'admin', type: 'create', module: '用户管理', description: '创建用户 user01', ip: '192.168.1.100', status: '成功', createTime: '2024-01-15 10:28:00' },
  { id: 3, username: 'user01', type: 'update', module: '用户管理', description: '更新用户资料', ip: '192.168.1.101', status: '成功', createTime: '2024-01-15 10:25:00' },
  { id: 4, username: 'admin', type: 'delete', module: '角色管理', description: '删除角色访客', ip: '192.168.1.100', status: '成功', createTime: '2024-01-15 10:20:00' },
  { id: 5, username: 'user01', type: 'export', module: '访问日志', description: '导出访问日志数据', ip: '192.168.1.101', status: '成功', createTime: '2024-01-15 10:15:00' },
  { id: 6, username: 'admin', type: 'create', module: '角色管理', description: '创建角色测试', ip: '192.168.1.100', status: '失败', createTime: '2024-01-15 10:10:00' },
]

const pagination = reactive({
  page: 1,
  size: 10,
  total: 89
})

const getTypeLabel = (type: string) => {
  const labels: Record<string, string> = {
    login: '登录',
    create: '创建',
    update: '更新',
    delete: '删除',
    export: '导出'
  }
  return labels[type] || type
}

const getTypeColor = (type: string) => {
  const colors: Record<string, any> = {
    login: 'primary',
    create: 'success',
    update: 'warning',
    delete: 'danger',
    export: 'info'
  }
  return colors[type] || 'info'
}
</script>

<style scoped>
.operation-log {
  height: 100%;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 500;
  color: #111827;
  margin: 0;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
