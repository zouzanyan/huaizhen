<template>
  <div class="kg-project-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>知识图谱项目</span>
          <el-button type="primary" @click="handleAdd">新建项目</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="项目名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="400" fixed="right">
          <template #default="{ row }">
            <el-dropdown trigger="click">
              <el-button type="primary" link>
                更多操作 <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleModelManage(row)">模型管理</el-dropdown-item>
                  <el-dropdown-item @click="handleExplore(row)">图谱探索</el-dropdown-item>
                  <el-dropdown-item @click="handleCorpus(row)">语料管理</el-dropdown-item>
                  <el-dropdown-item @click="handleTransform(row)">数据导入</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500">
      <el-form :model="form" label-width="80px">
        <el-form-item label="项目名称" required>
          <el-input v-model="form.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入项目描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import kgProject from '@/services/kgProject'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const dialogTitle = ref('新建项目')
const form = reactive({
  id: null,
  name: '',
  description: '',
  status: 1
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await kgProject.list({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新建项目'
  form.id = null
  form.name = ''
  form.description = ''
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑项目'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.name) {
    ElMessage.warning('请输入项目名称')
    return
  }
  try {
    if (form.id) {
      await kgProject.update(form)
    } else {
      await kgProject.create(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该项目吗？', '提示', { type: 'warning' })
    await kgProject.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleModelManage = (row) => {
  router.push(`/kg/model/${row.id}`)
}

const handleExplore = (row) => {
  router.push(`/kg/explore/${row.id}`)
}

const handleCorpus = (row) => {
  router.push(`/kg/corpus/${row.id}`)
}

const handleTransform = (row) => {
  router.push(`/kg/transform/${row.id}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.kg-project-management {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
