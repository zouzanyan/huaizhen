<template>
  <div class="kg-explore">
    <el-page-header @back="$router.back()" style="margin-bottom: 20px;">
      <template #content>
        <span>知识图谱探索 - {{ projectName }}</span>
      </template>
    </el-page-header>

    <el-row :gutter="20">
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>图谱可视化</span>
              <div>
                <el-button type="primary" size="small" @click="loadGraphData">刷新</el-button>
                <el-button type="danger" size="small" @click="handleClearGraph">清空图谱</el-button>
              </div>
            </div>
          </template>
          <div ref="chartRef" class="chart-container" v-loading="chartLoading"></div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <template #header>
            <span>LLM 知识抽取</span>
          </template>
          <el-form label-position="top">
            <el-form-item label="输入文本">
              <el-input
                v-model="extractionText"
                type="textarea"
                rows="6"
                placeholder="请输入需要抽取知识的文本..."
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handlePreviewExtraction" :loading="previewLoading">
                预览抽取结果
              </el-button>
              <el-button type="success" @click="handleExtractAndSave" :loading="extractLoading">
                抽取并保存
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div v-if="previewResult" class="preview-result">
            <h4>抽取预览</h4>
            <div class="result-section">
              <strong>实体：</strong>
              <el-tag
                v-for="(entity, index) in previewResult.entities"
                :key="index"
                :style="{ backgroundColor: getEntityColor(entity.type), color: '#fff', margin: '2px' }"
              >
                {{ entity.name }} ({{ entity.type }})
              </el-tag>
            </div>
            <div class="result-section" v-if="previewResult.relations && previewResult.relations.length">
              <strong>关系：</strong>
              <div v-for="(rel, index) in previewResult.relations" :key="index" class="relation-item">
                {{ rel.source }} → <strong>{{ rel.relation }}</strong> → {{ rel.target }}
              </div>
            </div>
          </div>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <span>图谱统计</span>
          </template>
          <el-descriptions :column="1" size="small">
            <el-descriptions-item label="节点数量">{{ graphData.nodes?.length || 0 }}</el-descriptions-item>
            <el-descriptions-item label="关系数量">{{ graphData.edges?.length || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import kgProject from '@/services/kgProject'
import kgGraph from '@/services/kgGraph'
import kgExtraction from '@/services/kgExtraction'
import kgEntityType from '@/services/kgEntityType'

const route = useRoute()
const projectId = computed(() => route.params.projectId)
const projectName = ref('')
const chartRef = ref(null)
const chartLoading = ref(false)
const chart = ref(null)
const graphData = reactive({ nodes: [], edges: [] })
const extractionText = ref('')
const previewLoading = ref(false)
const extractLoading = ref(false)
const previewResult = ref(null)
const entityTypeColors = ref({})

const loadProject = async () => {
  try {
    const res = await kgProject.getById(projectId.value)
    if (res.code === 200) {
      projectName.value = res.data.name
    }
  } catch (error) {
    ElMessage.error('加载项目信息失败')
  }
}

const loadEntityTypes = async () => {
  try {
    const res = await kgEntityType.listByProjectId(projectId.value)
    if (res.code === 200) {
      res.data.forEach(et => {
        entityTypeColors.value[et.name] = et.color || '#409EFF'
      })
    }
  } catch (error) {
    console.error('加载实体类型失败')
  }
}

const loadGraphData = async () => {
  chartLoading.value = true
  try {
    const res = await kgGraph.getGraphData(projectId.value)
    if (res.code === 200) {
      graphData.nodes = res.data.nodes || []
      graphData.edges = res.data.edges || []
      await nextTick()
      renderChart()
    }
  } catch (error) {
    ElMessage.error('加载图谱数据失败')
  } finally {
    chartLoading.value = false
  }
}

const getEntityColor = (type) => {
  return entityTypeColors.value[type] || '#409EFF'
}

const renderChart = () => {
  if (!chartRef.value) return

  if (!chart.value) {
    chart.value = echarts.init(chartRef.value)
  }

  const nodes = graphData.nodes.map(node => ({
    id: String(node.id),
    name: node.name,
    symbolSize: 50,
    category: node.entityTypeName,
    itemStyle: {
      color: node.color || getEntityColor(node.entityTypeName)
    },
    label: {
      show: true,
      position: 'bottom',
      formatter: node.name
    }
  }))

  const edges = graphData.edges.map(edge => ({
    source: String(edge.source),
    target: String(edge.target),
    value: edge.relationType,
    label: {
      show: true,
      formatter: edge.relationType
    },
    lineStyle: {
      curveness: 0.2
    }
  }))

  const categories = [...new Set(graphData.nodes.map(n => n.entityTypeName))].map(name => ({
    name: name
  }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        if (params.dataType === 'node') {
          return `${params.data.name}<br/>类型: ${params.data.category || '未知'}`
        } else {
          return `${params.data.source} → ${params.data.value} → ${params.data.target}`
        }
      }
    },
    legend: {
      data: categories.map(c => c.name),
      orient: 'vertical',
      right: 10,
      top: 10
    },
    series: [{
      type: 'graph',
      layout: 'force',
      data: nodes,
      links: edges,
      categories: categories,
      roam: true,
      draggable: true,
      label: {
        show: true,
        position: 'bottom'
      },
      force: {
        repulsion: 500,
        edgeLength: 150,
        gravity: 0.1
      },
      emphasis: {
        focus: 'adjacency',
        lineStyle: {
          width: 3
        }
      },
      edgeSymbol: ['none', 'arrow'],
      edgeSymbolSize: [4, 10]
    }]
  }

  chart.value.setOption(option)
}

const handlePreviewExtraction = async () => {
  if (!extractionText.value.trim()) {
    ElMessage.warning('请输入需要抽取的文本')
    return
  }
  previewLoading.value = true
  try {
    const res = await kgExtraction.previewExtraction({
      projectId: projectId.value,
      text: extractionText.value
    })
    if (res.code === 200) {
      previewResult.value = res.data
      if (previewResult.value.error) {
        ElMessage.error('抽取失败: ' + previewResult.value.error)
      }
    }
  } catch (error) {
    ElMessage.error('预览抽取失败')
  } finally {
    previewLoading.value = false
  }
}

const handleExtractAndSave = async () => {
  if (!extractionText.value.trim()) {
    ElMessage.warning('请输入需要抽取的文本')
    return
  }
  extractLoading.value = true
  try {
    const res = await kgExtraction.extractByLlm({
      projectId: projectId.value,
      text: extractionText.value
    })
    if (res.code === 200) {
      const result = res.data
      if (result.success) {
        ElMessage.success(`抽取成功！新增 ${result.entityCount} 个实体`)
        previewResult.value = null
        extractionText.value = ''
        loadGraphData()
      } else {
        ElMessage.error('抽取失败: ' + (result.error || '未知错误'))
      }
    }
  } catch (error) {
    ElMessage.error('抽取失败')
  } finally {
    extractLoading.value = false
  }
}

const handleClearGraph = async () => {
  try {
    await ElMessageBox.confirm('确定要清空该图谱的所有数据吗？此操作不可恢复！', '警告', { type: 'warning' })
    await kgGraph.clearProjectData(projectId.value)
    ElMessage.success('清空成功')
    loadGraphData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

onMounted(async () => {
  await loadProject()
  await loadEntityTypes()
  await loadGraphData()
  if (route.query.text) {
    extractionText.value = route.query.text
  }
  window.addEventListener('resize', () => {
    chart.value?.resize()
  })
})
</script>

<style scoped>
.kg-explore {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chart-container {
  width: 100%;
  height: 600px;
}
.preview-result {
  margin-top: 10px;
}
.result-section {
  margin-bottom: 10px;
}
.relation-item {
  padding: 4px 0;
  color: #666;
}
</style>
