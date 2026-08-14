<template>
  <div class="main-col">
    <div class="box post-detail" v-if="post">
      <div class="post-detail-header">
        <div class="post-detail-title">{{ post.title }}</div>
        <div class="post-detail-meta">
          <router-link v-if="boardName" :to="`/node/${post.boardId}`" class="tag">{{ boardName }}</router-link>
          <span>由 <strong>{{ authorName }}</strong></span>
          <span>· {{ timeAgo(post.createdAt) }}</span>
          <span>· {{ post.viewCount || 0 }} 次点击</span>
        </div>
      </div>
      <div class="post-detail-content">{{ post.content }}</div>
      <div class="box-body" style="display:flex;align-items:center;gap:12px;border-top:1px solid #f0f0f0;">
        <button class="btn btn-sm" :class="{ 'btn-primary': liked }" @click="toggleLike">
          {{ liked ? '已赞' : '赞' }} ({{ likeCnt }})
        </button>
        <button
          v-if="isAuthor"
          class="btn btn-sm"
          @click="handleDelete"
        >删除</button>
      </div>
    </div>

    <!-- 评论 -->
    <div class="box">
      <div class="box-header">
        <span>{{ comments.length }} 条回复</span>
      </div>
      <div v-if="comments.length === 0" class="empty">暂无回复，抢个沙发吧</div>
      <CommentItem
        v-for="(c, i) in comments"
        :key="c.id"
        :comment="c"
        :floor="i + 1"
      />
    </div>

    <!-- 回复框 -->
    <div class="box" v-if="isLogin()">
      <div class="box-header">添加回复</div>
      <div class="box-body">
        <div class="form-group">
          <textarea v-model="replyContent" class="form-control" rows="4" placeholder="写下你的回复..."></textarea>
        </div>
        <button class="btn btn-primary" :disabled="submitting" @click="submitReply">
          {{ submitting ? '发送中...' : '发送回复' }}
        </button>
      </div>
    </div>
    <div class="box" v-else>
      <div class="box-body" style="text-align:center;">
        <router-link :to="`/login?redirect=${encodeURIComponent('/post/' + postId)}`">登录</router-link> 后参与讨论
      </div>
    </div>
  </div>

  <Sidebar :boards="boards" />
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CommentItem from '@/components/CommentItem.vue'
import Sidebar from '@/components/Sidebar.vue'
import { getPostById, deletePost } from '@/api/post'
import { getBoardById, getAllBoards } from '@/api/board'
import { getUserById } from '@/api/user'
import { getCommentsByPost, createComment } from '@/api/comment'
import { like, unlike, checkLiked, likeCount } from '@/api/like'
import { useUser } from '@/store/user'
import { toast } from '@/utils/toast'

const route = useRoute()
const router = useRouter()
const { state, isLogin, getUserId } = useUser()

const postId = computed(() => route.params.id)
const post = ref(null)
const author = ref({})
const boardName = ref('')
const boards = ref([])
const comments = ref([])
const liked = ref(false)
const likeCnt = ref(0)
const replyContent = ref('')
const submitting = ref(false)

const authorName = computed(() => author.value.nickname || ('用户' + (post.value?.userId || '')))
const isAuthor = computed(() => isLogin() && post.value && String(getUserId()) === String(post.value.userId))

function timeAgo(ts) {
  if (!ts) return ''
  const d = new Date(typeof ts === 'string' ? ts.replace(' ', 'T') : ts)
  const diff = (Date.now() - d.getTime()) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + ' 分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + ' 小时前'
  if (diff < 2592000) return Math.floor(diff / 86400) + ' 天前'
  return d.toLocaleDateString('zh-CN')
}

async function loadPost() {
  try {
    post.value = await getPostById(postId.value)
    if (post.value && post.value.userId) {
      try { author.value = await getUserById(post.value.userId) } catch (e) { /* ignore */ }
    }
    if (post.value && post.value.boardId) {
      try {
        const b = await getBoardById(post.value.boardId)
        boardName.value = b.name
      } catch (e) { /* ignore */ }
    }
  } catch (e) {
    toast(e.message)
  }
}

async function loadBoards() {
  try { boards.value = await getAllBoards() } catch (e) { /* ignore */ }
}

async function loadComments() {
  try {
    comments.value = await getCommentsByPost(postId.value)
  } catch (e) { /* ignore */ }
}

async function loadLike() {
  try {
    const c = await likeCount(postId.value)
    likeCnt.value = c.count || 0
    if (isLogin()) {
      const r = await checkLiked(postId.value, getUserId())
      liked.value = !!r.liked
    } else {
      liked.value = false
    }
  } catch (e) { /* ignore */ }
}

async function toggleLike() {
  if (!isLogin()) {
    toast('请先登录')
    return router.push({ name: 'login', query: { redirect: '/post/' + postId.value } })
  }
  const uid = getUserId()
  try {
    if (liked.value) {
      await unlike(postId.value, uid)
      liked.value = false
      likeCnt.value = Math.max(0, likeCnt.value - 1)
    } else {
      await like(postId.value, uid)
      liked.value = true
      likeCnt.value += 1
    }
  } catch (e) {
    toast(e.message)
  }
}

async function submitReply() {
  if (!replyContent.value.trim()) {
    toast('回复内容不能为空')
    return
  }
  submitting.value = true
  try {
    await createComment({
      postId: Number(postId.value),
      userId: getUserId(),
      content: replyContent.value,
      status: 1
    })
    replyContent.value = ''
    await loadComments()
    toast('回复成功')
  } catch (e) {
    toast(e.message)
  } finally {
    submitting.value = false
  }
}

async function handleDelete() {
  if (!confirm('确认删除这个话题？')) return
  try {
    await deletePost(postId.value)
    toast('删除成功')
    router.push('/')
  } catch (e) {
    toast(e.message)
  }
}

watch(() => route.params.id, () => {
  loadAll()
})

function loadAll() {
  loadPost()
  loadBoards()
  loadComments()
  loadLike()
}

onMounted(loadAll)
</script>
