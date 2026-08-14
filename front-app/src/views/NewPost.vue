<template>
  <div class="main-col">
    <div class="box">
      <div class="box-header">创建新话题</div>
      <div class="box-body">
        <div class="form-group">
          <label>节点</label>
          <select v-model="form.boardId" class="form-control">
            <option value="">请选择节点</option>
            <option v-for="b in boards" :key="b.id" :value="b.id">{{ b.name }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>标题</label>
          <input v-model="form.title" class="form-control" placeholder="话题标题" />
        </div>
        <div class="form-group">
          <label>正文</label>
          <textarea v-model="form.content" class="form-control" rows="10" placeholder="话题正文..."></textarea>
        </div>
        <button class="btn btn-primary" :disabled="submitting" @click="submit">
          {{ submitting ? '发布中...' : '发布话题' }}
        </button>
        <router-link to="/" class="btn" style="margin-left:8px;">取消</router-link>
      </div>
    </div>
  </div>

  <Sidebar :boards="boards" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from '@/components/Sidebar.vue'
import { createPost } from '@/api/post'
import { getAllBoards } from '@/api/board'
import { useUser } from '@/store/user'
import { toast } from '@/utils/toast'

const router = useRouter()
const { getUserId } = useUser()
const boards = ref([])
const submitting = ref(false)
const form = ref({ boardId: '', title: '', content: '' })

async function loadBoards() {
  try { boards.value = await getAllBoards() } catch (e) { /* ignore */ }
}

async function submit() {
  if (!form.value.boardId) { toast('请选择节点'); return }
  if (!form.value.title.trim()) { toast('请填写标题'); return }
  if (!form.value.content.trim()) { toast('请填写正文'); return }

  submitting.value = true
  try {
    await createPost({
      userId: getUserId(),
      boardId: Number(form.value.boardId),
      title: form.value.title,
      content: form.value.content,
      status: 1
    })
    toast('发布成功')
    router.push('/')
  } catch (e) {
    toast(e.message)
  } finally {
    submitting.value = false
  }
}

onMounted(loadBoards)
</script>
