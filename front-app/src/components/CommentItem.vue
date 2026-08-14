<template>
  <div class="comment-item">
    <Avatar :name="comment.nickname || '匿'" />
    <div class="comment-main">
      <div class="comment-head">
        <span class="comment-author">{{ comment.nickname || '匿名用户' }}</span>
        <span>· {{ timeAgo(comment.createdAt) }}</span>
        <span class="floor">#{{ floor }}</span>
      </div>
      <div class="comment-content">{{ comment.content }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  comment: { type: Object, required: true },
  floor: { type: Number, default: 0 }
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
