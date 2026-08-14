import { createRouter, createWebHistory } from 'vue-router'
import { useUser } from '@/store/user'

const routes = [
  { path: '/', name: 'home', component: () => import('@/views/Home.vue') },
  { path: '/post/:id', name: 'post', component: () => import('@/views/PostDetail.vue') },
  { path: '/node/:id', name: 'node', component: () => import('@/views/Node.vue') },
  { path: '/new', name: 'new', component: () => import('@/views/NewPost.vue'), meta: { requiresAuth: true } },
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('@/views/Register.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth) {
    const { isLogin } = useUser()
    if (!isLogin()) {
      return { name: 'login', query: { redirect: to.fullPath } }
    }
  }
})

export default router
