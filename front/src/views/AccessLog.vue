<template>
  <div class="access-log">
    <div class="page-header">
      <h2 class="page-title">访问日志</h2>
    </div>

    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入关键词"
          clearable
          style="width: 200px"
        />
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
        <el-button :icon="Download">导出</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="url" label="访问路径" min-width="200" />
        <el-table-column prop="method" label="请求方式" width="90">
          <template #default="{ row }">
            <el-tag :type="getMethodType(row.method)" size="small">
              {{ row.method }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userAgent" label="用户代理" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态码" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="耗时(ms)" width="100" />
        <el-table-column prop="createTime" label="访问时间" width="160" />
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
import { Search, Refresh, Download } from '@element-plus/icons-vue'

interface AccessLog {
  id: number
  username: string
  ip: string
  url: string
  method: string
  userAgent: string
  status: number
  duration: number
  createTime: string
}

const searchForm = reactive({
  keyword: '',
  dateRange: null as Date[] | null
})

const tableData: AccessLog[] = [
  { id: 1, username: 'admin', ip: '192.168.1.100', url: '/api/users', method: 'GET', userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', status: 200, duration: 45, createTime: '2024-01-15 10:30:00' },
  { id: 2, username: 'user01', ip: '192.168.1.101', url: '/api/users/1', method: 'GET', userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', status: 200, duration: 32, createTime: '2024-01-15 10:28:00' },
  { id: 3, username: 'user02', ip: '192.168.1.102', url: '/api/users', method: 'POST', userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', status: 201, duration: 120, createTime: '2024-01-15 10:25:00' },
  { id: 4, username: 'admin', ip: '192.168.1.100', url: '/api/users/999', method: 'GET', userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', status: 404, duration: 18, createTime: '2024-01-15 10:20:00' },
  { id: 5, username: 'user01', ip: '192.168.1.101', url: '/api/users/1', method: 'DELETE', userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', status: 200, duration: 65, createTime: '2024-01-15 10:15:00' },
]

const pagination = reactive({
  page: 1,
  size: 10,
  total: 156
})

const getMethodType = (method: string) => {
  const types: Record<string, any> = {
    GET: '',
    POST: 'success',
    PUT: 'warning',
    DELETE: 'danger'
  }
  return types[method] || 'info'
}

const getStatusType = (status: number) => {
  if (status >= 200 && status < 300) return 'success'
  if (status >= 300 && status < 400) return 'warning'
  if (status >= 400 && status < 500) return 'info'
  if (status >= 500) return 'danger'
  return 'info'
}
</script>

<style scoped>
.access-log {
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
