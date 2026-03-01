import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/Layout.vue'
import auth from '../services/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginSimple.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue')
      },
      {
        path: 'system/user',
        name: 'UserManagement',
        component: () => import('../views/UserManagement.vue')
      },
      {
        path: 'system/role',
        name: 'RoleManagement',
        component: () => import('../views/RoleManagement.vue')
      },
      {
        path: 'system/menu',
        name: 'MenuManagement',
        component: () => import('../views/MenuManagement.vue')
      },
      {
        path: 'forum/post',
        name: 'ForumPostManagement',
        component: () => import('../views/ForumPostManagement.vue')
      },
      {
        path: 'forum/board',
        name: 'ForumBoardManagement',
        component: () => import('../views/ForumBoardManagement.vue')
      },
      {
        path: 'forum/user',
        name: 'ForumUserManagement',
        component: () => import('../views/ForumUserManagement.vue')
      },
      {
        path: 'mock/data',
        name: 'MockData',
        component: () => import('../views/MockData.vue')
      },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = auth.isAuthenticated()
  const token = auth.getToken()

  console.log('路由守卫:', {
    to: to.path,
    from: from.path,
    isAuthenticated,
    hasToken: !!token,
    requiresAuth: to.meta.requiresAuth
  })

  if (to.meta.requiresAuth && !isAuthenticated) {
    // 需要认证但未登录，跳转到登录页
    console.log('需要认证，跳转到登录页')
    next('/login')
  } else if (to.path === '/login' && isAuthenticated) {
    // 已登录用户访问登录页，跳转到首页
    console.log('已登录，跳转到首页')
    next('/')
  } else {
    console.log('允许访问路由:', to.path)
    next()
  }
})

export default router
