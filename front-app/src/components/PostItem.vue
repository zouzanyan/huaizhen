<template>
  <div class="post-item">
    <Avatar :name="authorName" />
    <div class="post-main">
      <router-link :to="`/post/${post.id}`" class="post-title">{{ post.title }}</router-link>
      <div class="post-meta">
        <router-link v-if="boardName" :to="`/node/${post.boardId}`" class="tag">{{ boardName }}</router-link>
        <span>{{ authorName }}</span>
        <span>·</span>
        <span>{{ timeAgo(post.createdAt) }}</span>
      </div>
    </div>
    <div class="reply-count">
      <span class="count">{{ post.commentCount || 0 }}</span>
      <span class="label">回复</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import Avatar from '@/components/Avatar.vue'

const props = defineProps({
  post: { type: Object, required: true },
  boardName: { type: String, default: '' },
  authorName: { type: String, default: '匿名' }
})

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
</script>
