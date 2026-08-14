<template>
  <div class="main-col">
    <div class="box">
      <div class="box-header">
        <span>{{ board.name || '节点' }}</span>
        <router-link to="/" class="tag">回首页</router-link>
      </div>
      <div class="box-body" v-if="board.description" style="color: var(--color-gray); font-size: 13px;">
        {{ board.description }}
      </div>
      <div v-if="loading" class="empty">加载中...</div>
      <div v-else-if="posts.length === 0" class="empty">该节点下暂无话题</div>
      <template v-else>
        <PostItem
          v-for="p in posts"
          :key="p.id"
          :post="p"
          :board-name="board.name"
          :author-name="authorName(p.userId)"
        />
      </template>
    </div>

    <div class="pagination" v-if="total > size">
      <button class="btn btn-sm" :disabled="page <= 1" @click="changePage(page - 1)">上一页</button>
      <span>第 {{ page }} 页</span>
      <button class="btn btn-sm" :disabled="page * size >= total" @click="changePage(page + 1)">下一页</button>
    </div>
  </div>

  <Sidebar :boards="boards" />
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import PostItem from '@/components/PostItem.vue'
import Sidebar from '@/components/Sidebar.vue'
import { getPostList } from '@/api/post'
import { getBoardById, getAllBoards } from '@/api/board'
import { getUserList } from '@/api/user'
import { toast } from '@/utils/toast'

const route = useRoute()
const posts = ref([])
const boards = ref([])
const users = ref([])
const board = ref({})
const total = ref(0)
const page = ref(1)
const size = ref(20)
const loading = ref(false)

function authorName(id) {
  const u = users.value.find(x => String(x.id) === String(id))
  return u ? u.nickname : '用户' + id
}

async function loadBoard() {
  try {
    board.value = await getBoardById(route.params.id)
  } catch (e) { /* ignore */ }
}

async function loadBoards() {
  try {
    boards.value = await getAllBoards()
  } catch (e) { /* ignore */ }
}

async function loadUsers() {
  try {
    const data = await getUserList({ page: 1, size: 1000 })
    users.value = data.list || []
  } catch (e) { /* ignore */ }
}

async function loadPosts() {
  loading.value = true
  try {
    const data = await getPostList({ page: page.value, size: size.value, status: 1, boardId: route.params.id })
    posts.value = data.list || []
    total.value = data.total || 0
  } catch (e) {
    toast(e.message)
  } finally {
    loading.value = false
  }
}

function changePage(p) {
  page.value = p
  loadPosts()
}

watch(() => route.params.id, () => {
  page.value = 1
  loadBoard()
  loadPosts()
})

onMounted(() => {
  loadBoard()
  loadBoards()
  loadUsers()
  loadPosts()
})
</script>
