<template>
  <aside class="side-col">
    <!-- 当前用户 -->
    <div class="box" v-if="isLogin()">
      <div class="box-body" style="display:flex;align-items:center;gap:10px;">
        <Avatar :name="state.user.nickname" size="lg" />
        <div>
          <div style="font-weight:600;">{{ state.user.nickname }}</div>
          <div style="font-size:12px;color:var(--color-gray);">@{{ state.user.username }}</div>
        </div>
      </div>
    </div>

    <!-- 发帖入口 -->
    <div class="box">
      <div class="box-body">
        <router-link v-if="isLogin()" to="/new" class="btn btn-primary" style="width:100%;text-align:center;">
          创建新话题
        </router-link>
        <template v-else>
          <p style="margin-bottom:10px;color:var(--color-gray);">加入怀真社区，参与讨论。</p>
          <router-link to="/login" class="btn btn-primary" style="width:100%;text-align:center;">登录</router-link>
          <router-link to="/register" class="btn" style="width:100%;text-align:center;margin-top:8px;">注册</router-link>
        </template>
      </div>
    </div>

    <!-- 节点导航 -->
    <div class="box">
      <div class="box-header">节点导航</div>
      <div class="box-body">
        <div v-if="boards.length === 0" class="empty" style="padding:16px 0;">暂无节点</div>
        <div v-else class="node-list">
          <router-link v-for="b in boards" :key="b.id" :to="`/node/${b.id}`" class="tag">{{ b.name }}</router-link>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import Avatar from '@/components/Avatar.vue'
import { useUser } from '@/store/user'

defineProps({
  boards: { type: Array, default: () => [] }
})

const { state, isLogin } = useUser()
</script>
