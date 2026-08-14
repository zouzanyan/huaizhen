import { reactive } from 'vue'

const STORAGE_KEY = 'forum_user'

const state = reactive({
  user: JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null')
})

export function useUser() {
  function setUser(user) {
    state.user = user
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
  }

  function logout() {
    state.user = null
    localStorage.removeItem(STORAGE_KEY)
  }

  function isLogin() {
    return !!state.user
  }

  function getUserId() {
    return state.user ? Number(state.user.id) : null
  }

  return { state, setUser, logout, isLogin, getUserId }
}
