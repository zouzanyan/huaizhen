<template>
  <div class="dashboard">
    <div class="page-header">
      <h2 class="page-title">仪表盘</h2>
      <p class="page-subtitle">知识图谱协同构建管理系统</p>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon primary">
              <el-icon><Folder /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.projectCount }}</div>
              <div class="stat-label">项目总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><Collection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.entityTypeCount }}</div>
              <div class="stat-label">实体类型</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.relationTypeCount }}</div>
              <div class="stat-label">关系类型</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon info">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.corpusCount }}</div>
              <div class="stat-label">语料数量</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon purple">
              <el-icon><Share /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.nodeCount }}</div>
              <div class="stat-label">图谱节点</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon danger">
              <el-icon><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.relationCount }}</div>
              <div class="stat-label">图谱关系</div>
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
              <span>最近项目</span>
              <el-button type="primary" link @click="router.push('/kg/project')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentProjects" style="width: 100%" v-loading="loading">
            <el-table-column prop="name" label="项目名称" />
            <el-table-column prop="nodeCount" label="节点数" width="80" />
            <el-table-column prop="relationCount" label="关系数" width="80" />
            <el-table-column prop="createdAt" label="创建时间" width="160" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近语料</span>
              <el-button type="primary" link @click="router.push('/kg/corpus')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentCorpus" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="标题" show-overflow-tooltip />
            <el-table-column prop="projectName" label="所属项目" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'info' : 'success'" size="small">
                  {{ row.status === 1 ? '未处理' : '已处理' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="router.push('/kg/project')">
              <el-icon><Folder /></el-icon>
              项目管理
            </el-button>
            <el-button type="success" @click="router.push('/kg/model')">
              <el-icon><Collection /></el-icon>
              模型管理
            </el-button>
            <el-button type="warning" @click="router.push('/kg/explore')">
              <el-icon><Share /></el-icon>
              图谱探索
            </el-button>
            <el-button type="info" @click="router.push('/kg/corpus')">
              <el-icon><Document /></el-icon>
              语料管理
            </el-button>
            <el-button @click="router.push('/kg/transform')">
              <el-icon><Upload /></el-icon>
              数据导入
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Folder, Collection, Connection, Document, Share, DataLine, Upload } from '@element-plus/icons-vue'
import dashboard from '@/services/dashboard'

const router = useRouter()
const loading = ref(false)

const stats = reactive({
  projectCount: 0,
  entityTypeCount: 0,
  relationTypeCount: 0,
  corpusCount: 0,
  nodeCount: 0,
  relationCount: 0
})

const recentProjects = ref([])
const recentCorpus = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const [statsRes, projectsRes, corpusRes] = await Promise.all([
      dashboard.getStats(),
      dashboard.getRecentProjects(),
      dashboard.getRecentCorpus()
    ])

    if (statsRes.code === 200) {
      Object.assign(stats, statsRes.data)
    }
    if (projectsRes.code === 200) {
      recentProjects.value = projectsRes.data
    }
    if (corpusRes.code === 200) {
      recentCorpus.value = corpusRes.data
    }
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
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
  gap: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
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

.stat-icon.info {
  background-color: #e0f2fe;
  color: #0ea5e9;
}

.stat-icon.purple {
  background-color: #ede9fe;
  color: #8b5cf6;
}

.stat-icon.danger {
  background-color: #fee2e2;
  color: #ef4444;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #111827;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.content-row {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #111827;
}

.quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>
