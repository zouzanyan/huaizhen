<template>
  <span class="avatar" :class="sizeClass" :style="{ background: bgColor }">
    {{ initial }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: { type: String, default: '' },
  size: { type: String, default: '' } // '' | 'lg'
})

const initial = computed(() => props.name ? props.name.charAt(0).toUpperCase() : '?')

const bgColor = computed(() => {
  const colors = ['#778087', '#f60', '#5cb85c', '#4a90d9', '#d9534f', '#9b59b6', '#e67e22', '#16a085']
  let hash = 0
  for (let i = 0; i < props.name.length; i++) {
    hash = props.name.charCodeAt(i) + ((hash << 5) - hash)
  }
  return colors[Math.abs(hash) % colors.length]
})

const sizeClass = computed(() => props.size === 'lg' ? 'avatar-lg' : '')
</script>
