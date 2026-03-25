<template>
  <div class="mock-data">
    <div class="page-header">
      <h2 class="page-title">Mock 数据生成器</h2>
      <el-button type="info" :icon="Refresh" @click="loadStats">刷新统计</el-button>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
          <div class="stat-label">用户数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalForums || 0 }}</div>
          <div class="stat-label">版块数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalPosts || 0 }}</div>
          <div class="stat-label">帖子数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalComments || 0 }}</div>
          <div class="stat-label">评论数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalLikes || 0 }}</div>
          <div class="stat-label">点赞数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalFavorites || 0 }}</div>
          <div class="stat-label">收藏数</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作标签页 -->
    <el-card class="main-card">
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 创建用户 -->
        <el-tab-pane label="创建用户" name="user">
          <el-form :model="userForm" label-width="100px" label-position="left">
            <el-form-item label="用户名">
              <el-input
                v-model="userForm.username"
                placeholder="留空自动生成"
                clearable
              />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input
                v-model="userForm.nickname"
                placeholder="留空随机选择"
                clearable
              />
            </el-form-item>
            <el-form-item label="密码">
              <el-input
                v-model="userForm.password"
                type="password"
                placeholder="留空使用默认密码: mockmockmockhahahaha"
                show-password
                clearable
              />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input
                v-model="userForm.email"
                placeholder="留空自动生成"
                clearable
              />
            </el-form-item>
            <el-form-item label="头像URL">
              <el-input
                v-model="userForm.avatarUrl"
                placeholder="留空随机生成"
                clearable
              />
            </el-form-item>
            <el-form-item label="角色">
              <el-radio-group v-model="userForm.role">
                <el-radio :label="0">普通用户</el-radio>
                <el-radio :label="1">管理员</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="userForm.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Plus" @click="createUser" :loading="loading">
                创建用户
              </el-button>
              <el-button @click="resetUserForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 创建帖子 -->
        <el-tab-pane label="创建帖子" name="post">
          <el-form :model="postForm" label-width="100px" label-position="left">
            <el-form-item label="版块ID" required>
              <el-input
                v-model="postForm.forumId"
                placeholder="请输入版块ID"
                clearable
              />
              <el-text size="small" type="info" style="margin-left: 10px">
                提示：先去版块管理查看可用版块ID（支持复制粘贴）
              </el-text>
            </el-form-item>
            <el-form-item label="用户ID" required>
              <el-input
                v-model="postForm.userId"
                placeholder="请输入用户ID"
                clearable
              />
              <el-text size="small" type="info" style="margin-left: 10px">
                提示：使用上面创建的用户ID（支持复制粘贴）
              </el-text>
            </el-form-item>
            <el-form-item label="标题" required>
              <el-input
                v-model="postForm.title"
                placeholder="请输入帖子标题"
                clearable
              />
            </el-form-item>
            <el-form-item label="内容" required>
              <el-input
                v-model="postForm.content"
                type="textarea"
                :rows="8"
                placeholder="请输入帖子内容，支持 Markdown"
              />
            </el-form-item>
            <el-form-item label="内容类型">
              <el-radio-group v-model="postForm.contentType">
                <el-radio :label="0">纯文本</el-radio>
                <el-radio :label="1">Markdown</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="postForm.status">
                <el-radio :label="0">草稿</el-radio>
                <el-radio :label="1">已发布</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Plus" @click="createPost" :loading="loading">
                创建帖子
              </el-button>
              <el-button @click="resetPostForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 创建评论 -->
        <el-tab-pane label="创建评论" name="comment">
          <el-form :model="commentForm" label-width="100px" label-position="left">
            <el-form-item label="帖子ID" required>
              <el-input
                v-model="commentForm.postId"
                placeholder="请输入帖子ID"
                clearable
              />
            </el-form-item>
            <el-form-item label="用户ID" required>
              <el-input
                v-model="commentForm.userId"
                placeholder="请输入用户ID"
                clearable
              />
            </el-form-item>
            <el-form-item label="父评论ID">
              <el-input
                v-model="commentForm.parentId"
                placeholder="留空则为顶层评论，有值则为回复"
                clearable
              />
              <el-text size="small" type="info" style="margin-left: 10px">
                留空为评论，填写则为回复
              </el-text>
            </el-form-item>
            <el-form-item label="评论内容" required>
              <el-input
                v-model="commentForm.content"
                type="textarea"
                :rows="4"
                placeholder="请输入评论内容"
              />
            </el-form-item>
            <el-form-item label="点赞数">
              <el-input-number
                v-model="commentForm.likes"
                :min="0"
                placeholder="初始点赞数"
              />
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="commentForm.status">
                <el-radio :label="0">隐藏</el-radio>
                <el-radio :label="1">显示</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Plus" @click="createComment" :loading="loading">
                创建评论
              </el-button>
              <el-button @click="resetCommentForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 快速操作 -->
        <el-tab-pane label="快速操作" name="quick">
          <el-form :model="quickForm" label-width="100px" label-position="left">
            <el-divider content-position="left">创建点赞</el-divider>
            <el-form-item label="帖子ID" required>
              <el-input v-model="quickForm.likePostId" placeholder="帖子ID" clearable />
            </el-form-item>
            <el-form-item label="用户ID" required>
              <el-input v-model="quickForm.likeUserId" placeholder="用户ID" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="success" :icon="StarFilled" @click="createLike" :loading="loading">
                点赞
              </el-button>
            </el-form-item>

            <el-divider content-position="left">创建收藏</el-divider>
            <el-form-item label="帖子ID" required>
              <el-input v-model="quickForm.favPostId" placeholder="帖子ID" clearable />
            </el-form-item>
            <el-form-item label="用户ID" required>
              <el-input v-model="quickForm.favUserId" placeholder="用户ID" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="warning" :icon="Star" @click="createFavorite" :loading="loading">
                收藏
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 批量生成 -->
        <el-tab-pane label="批量生成" name="batch">
          <el-alert
            title="批量生成说明"
            type="info"
            :closable="false"
            style="margin-bottom: 20px"
          >
            <template #default>
              <p>1. 先创建1个用户，记下返回的用户ID</p>
              <p>2. 使用该用户ID创建多个帖子</p>
              <p>3. 使用帖子和用户ID创建评论和互动</p>
            </template>
          </el-alert>

          <el-steps :active="batchStep" finish-status="success" align-center style="margin-bottom: 30px">
            <el-step title="创建用户" description="第一步" />
            <el-step title="创建帖子" description="第二步" />
            <el-step title="创建评论" description="第三步" />
            <el-step title="添加互动" description="第四步" />
          </el-steps>

          <div v-if="batchStep === 0" class="batch-step">
            <h3>步骤1：创建用户</h3>
            <el-button type="primary" @click="batchCreateUser" :loading="loading">
              创建一个测试用户
            </el-button>
            <el-text v-if="createdUserId" type="success" style="margin-left: 20px">
              已创建用户ID: {{ createdUserId }}
            </el-text>
          </div>

          <div v-if="batchStep === 1" class="batch-step">
            <h3>步骤2：批量创建帖子（使用用户ID: {{ createdUserId }}）</h3>
            <el-input-number v-model="batchPostCount" :min="1" :max="20" placeholder="帖子数量" />
            <el-button type="primary" @click="batchCreatePosts" :loading="loading" style="margin-left: 10px">
              生成帖子
            </el-button>
            <el-text v-if="createdPostIds.length" type="success" style="margin-left: 20px">
              已创建 {{ createdPostIds.length }} 个帖子
            </el-text>
          </div>

          <div v-if="batchStep === 2" class="batch-step">
            <h3>步骤3：为每个帖子添加评论</h3>
            <el-input-number v-model="batchCommentCount" :min="1" :max="10" placeholder="每个帖子的评论数" />
            <el-button type="primary" @click="batchCreateComments" :loading="loading" style="margin-left: 10px">
              生成评论
            </el-button>
            <el-text v-if="createdCommentCount > 0" type="success" style="margin-left: 20px">
              已创建 {{ createdCommentCount }} 条评论
            </el-text>
          </div>

          <div v-if="batchStep === 3" class="batch-step">
            <h3>步骤4：添加点赞和收藏</h3>
            <el-button type="success" @click="batchCreateInteractions" :loading="loading">
              随机添加互动
            </el-button>
          </div>

          <div style="margin-top: 30px">
            <el-button v-if="batchStep > 0" @click="batchStep--">上一步</el-button>
            <el-button v-if="batchStep < 3 && canNextStep" type="primary" @click="batchStep++">
              下一步
            </el-button>
            <el-button v-if="batchStep === 3" type="success" @click="resetBatch">
              重新开始
            </el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, StarFilled, Star } from '@element-plus/icons-vue'
import mockService from '../services/mock'

const activeTab = ref('user')
const loading = ref(false)

// 统计数据
const stats = reactive({
  totalUsers: 0,
  totalForums: 0,
  totalPosts: 0,
  totalComments: 0,
  totalLikes: 0,
  totalFavorites: 0
})

// 用户表单
const userForm = reactive({
  username: '',
  nickname: '',
  password: '',
  email: '',
  avatarUrl: '',
  role: 0,
  status: 1
})

// 帖子表单
const postForm = reactive({
  forumId: '',
  userId: '',
  title: '',
  content: '',
  contentType: 1,
  status: 1
})

// 评论表单
const commentForm = reactive({
  postId: '',
  userId: '',
  parentId: '',
  content: '',
  likes: 0,
  status: 1
})

// 快速操作表单
const quickForm = reactive({
  likePostId: '',
  likeUserId: '',
  favPostId: '',
  favUserId: ''
})

// 批量生成
const batchStep = ref(0)
const createdUserId = ref(null)
const createdPostIds = ref([])
const createdCommentCount = ref(0)
const batchPostCount = ref(5)
const batchCommentCount = ref(3)

const canNextStep = computed(() => {
  switch (batchStep.value) {
    case 0: return createdUserId.value !== null
    case 1: return createdPostIds.value.length > 0
    case 2: return createdCommentCount.value > 0
    default: return true
  }
})

// 加载统计
const loadStats = async () => {
  try {
    const response = await mockService.getStats()
    if (response.data.code === 200) {
      Object.assign(stats, response.data.data)
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 创建用户
const createUser = async () => {
  loading.value = true
  try {
    const response = await mockService.createUser(userForm)
    if (response.data.code === 200) {
      ElMessage.success(`用户创建成功！ID: ${response.data.data}`)
      resetUserForm()
      await loadStats()
    } else {
      ElMessage.error(response.data.message || '创建失败')
    }
  } catch (error) {
    ElMessage.error('创建失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const resetUserForm = () => {
  Object.assign(userForm, {
    username: '',
    nickname: '',
    password: '',
    email: '',
    avatarUrl: '',
    role: 0,
    status: 1
  })
}

// 创建帖子
const createPost = async () => {
  if (!postForm.forumId || !postForm.userId) {
    ElMessage.warning('请填写版块ID和用户ID')
    return
  }
  if (!postForm.title || !postForm.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }

  loading.value = true
  try {
    const response = await mockService.createPost(postForm)
    if (response.data.code === 200) {
      ElMessage.success(`帖子创建成功！ID: ${response.data.data}`)
      resetPostForm()
      await loadStats()
    } else {
      ElMessage.error(response.data.message || '创建失败')
    }
  } catch (error) {
    ElMessage.error('创建失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const resetPostForm = () => {
  Object.assign(postForm, {
    forumId: '',
    userId: '',
    title: '',
    content: '',
    contentType: 1,
    status: 1
  })
}

// 创建评论
const createComment = async () => {
  if (!commentForm.postId || !commentForm.userId) {
    ElMessage.warning('请填写帖子ID和用户ID')
    return
  }
  if (!commentForm.content) {
    ElMessage.warning('请填写评论内容')
    return
  }

  loading.value = true
  try {
    const response = await mockService.createComment(commentForm)
    if (response.data.code === 200) {
      ElMessage.success(`评论创建成功！ID: ${response.data.data}`)
      resetCommentForm()
      await loadStats()
    } else {
      ElMessage.error(response.data.message || '创建失败')
    }
  } catch (error) {
    ElMessage.error('创建失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const resetCommentForm = () => {
  Object.assign(commentForm, {
    postId: '',
    userId: '',
    parentId: '',
    content: '',
    likes: 0,
    status: 1
  })
}

// 创建点赞
const createLike = async () => {
  if (!quickForm.likePostId || !quickForm.likeUserId) {
    ElMessage.warning('请填写帖子ID和用户ID')
    return
  }

  loading.value = true
  try {
    const response = await mockService.createLike(quickForm.likePostId, quickForm.likeUserId)
    if (response.data.code === 200) {
      ElMessage.success('点赞成功！')
      await loadStats()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 创建收藏
const createFavorite = async () => {
  if (!quickForm.favPostId || !quickForm.favUserId) {
    ElMessage.warning('请填写帖子ID和用户ID')
    return
  }

  loading.value = true
  try {
    const response = await mockService.createFavorite(quickForm.favPostId, quickForm.favUserId)
    if (response.data.code === 200) {
      ElMessage.success('收藏成功！')
      await loadStats()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 批量创建用户
const batchCreateUser = async () => {
  loading.value = true
  try {
    const response = await mockService.createUser({
      nickname: '批量测试用户',
      role: 0,
      status: 1
    })
    if (response.data.code === 200) {
      createdUserId.value = response.data.data
      ElMessage.success(`用户创建成功！ID: ${createdUserId.value}`)
      await loadStats()
    }
  } catch (error) {
    ElMessage.error('创建失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 批量创建帖子
const batchCreatePosts = async () => {
  loading.value = true
  createdPostIds.value = []

  const titles = [
    '如何优化数据库查询性能？',
    'Spring Boot 最佳实践分享',
    '微服务架构设计经验总结',
    '前端性能优化的10个技巧',
    'Docker 容器化部署指南',
    'Redis 缓存设计模式',
    'TypeScript 高级用法详解',
    '消息队列如何保证消息不丢失？',
    '分布式事务解决方案对比',
    '代码审查的最佳实践'
  ]

  const contents = [
    '大家好，今天想和大家分享一些开发经验。\n\n## 核心要点\n\n1. 理解业务需求\n2. 选择合适的技术栈\n3. 注重代码质量\n\n希望能帮助到有需要的朋友！',
    '最近在项目中使用了一些新技术，效果非常不错。\n\n## 使用场景\n\n...\n\n## 实现步骤\n\n...\n\n欢迎大家一起讨论！',
    '关于这个问题，我有几个想请教大家：\n\n1. 如何处理高并发？\n2. 如何保证数据一致性？\n\n谢谢！'
  ]

  try {
    for (let i = 0; i < batchPostCount.value; i++) {
      const response = await mockService.createPost({
        forumId: 1,
        userId: createdUserId.value,
        title: titles[i % titles.length],
        content: contents[i % contents.length],
        contentType: 1,
        status: 1
      })
      if (response.data.code === 200) {
        createdPostIds.value.push(response.data.data)
      }
    }
    ElMessage.success(`成功创建 ${createdPostIds.value.length} 个帖子！`)
    await loadStats()
  } catch (error) {
    ElMessage.error('批量创建失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 批量创建评论
const batchCreateComments = async () => {
  loading.value = true
  createdCommentCount.value = 0

  const comments = [
    '很棒的分享！学到了很多',
    '请问博主有代码示例吗？',
    '正好遇到这个问题，太及时了！',
    '楼主辛苦了，感谢分享',
    '这个方法我试过了，确实有效',
    '有个疑问，希望能解答一下',
    '赞！期待更多这样的文章',
    '收藏了，慢慢研究',
    '这个思路不错，可以试试',
    '感谢楼主的详细讲解'
  ]

  try {
    for (const postId of createdPostIds.value) {
      for (let i = 0; i < batchCommentCount.value; i++) {
        await mockService.createComment({
          postId: postId,
          userId: createdUserId.value,
          content: comments[i % comments.length],
          status: 1,
          likes: Math.floor(Math.random() * 10)
        })
        createdCommentCount.value++
      }
    }
    ElMessage.success(`成功创建 ${createdCommentCount.value} 条评论！`)
    await loadStats()
  } catch (error) {
    ElMessage.error('批量创建失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 批量创建互动
const batchCreateInteractions = async () => {
  loading.value = true
  let count = 0

  try {
    for (const postId of createdPostIds.value) {
      // 50%概率点赞
      if (Math.random() > 0.5) {
        await mockService.createLike(postId, createdUserId.value)
        count++
      }
      // 30%概率收藏
      if (Math.random() > 0.7) {
        await mockService.createFavorite(postId, createdUserId.value)
        count++
      }
    }
    ElMessage.success(`成功添加 ${count} 个互动！`)
    await loadStats()
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 重置批量生成
const resetBatch = () => {
  batchStep.value = 0
  createdUserId.value = null
  createdPostIds.value = []
  createdCommentCount.value = 0
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.mock-data {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.main-card {
  min-height: 600px;
}

.batch-step {
  padding: 20px;
  text-align: center;
}

.batch-step h3 {
  margin-bottom: 20px;
  color: #303133;
}
</style>
