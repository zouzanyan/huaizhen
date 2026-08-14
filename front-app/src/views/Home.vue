<template>
  <div class="main-col">
    <div class="box">
      <div class="box-header">
        <span>怀真 / 最新话题</span>
        <span style="font-size: 12px;">共 {{ total }} 个话题</span>
      </div>
      <div v-if="loading" class="empty">加载中...</div>
      <div v-else-if="posts.length === 0" class="empty">还没有话题，来发第一个吧</div>
      <template v-else>
        <PostItem
          v-for="p in posts"
          :key="p.id"
          :post="p"
          :board-name="boardName(p.boardId)"
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
import { getAllBoards } from '@/api/board'
import { getUserList } from '@/api/user'
import { toast } from '@/utils/toast'

const route = useRoute()
const posts = ref([])
const boards = ref([])
const users = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const loading = ref(false)

function boardName(id) {
  const b = boards.value.find(x => String(x.id) === String(id))
  return b ? b.name : ''
}

function authorName(id) {
  const u = users.value.find(x => String(x.id) === String(id))
  return u ? u.nickname : '用户' + id
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
    const params = { page: page.value, size: size.value, status: 1 }
        if (route.query.keyword) params.keyword = route.query.keyword
    const data = await getPostList(params)
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

watch(() => route.query.keyword, () => {
  page.value = 1
  loadPosts()
})

onMounted(() => {
  loadBoards()
  loadUsers()
  loadPosts()
})
</script>
