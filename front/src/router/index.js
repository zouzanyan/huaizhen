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
        path: 'kg/project',
        name: 'KgProjectManagement',
        component: () => import('../views/KgProjectManagement.vue')
      },
      {
        path: 'kg/model',
        name: 'KgModelManagementSelect',
        component: () => import('../views/KgModelManagement.vue')
      },
      {
        path: 'kg/model/:projectId',
        name: 'KgModelManagement',
        component: () => import('../views/KgModelManagement.vue')
      },
      {
        path: 'kg/explore',
        name: 'KgExploreSelect',
        component: () => import('../views/KgExplore.vue')
      },
      {
        path: 'kg/explore/:projectId',
        name: 'KgExplore',
        component: () => import('../views/KgExplore.vue')
      },
      {
        path: 'kg/corpus',
        name: 'KgCorpusManagementSelect',
        component: () => import('../views/KgCorpusManagement.vue')
      },
      {
        path: 'kg/corpus/:projectId',
        name: 'KgCorpusManagement',
        component: () => import('../views/KgCorpusManagement.vue')
      },
      {
        path: 'kg/transform',
        name: 'KgTransformSelect',
        component: () => import('../views/KgTransform.vue')
      },
      {
        path: 'kg/transform/:projectId',
        name: 'KgTransform',
        component: () => import('../views/KgTransform.vue')
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
