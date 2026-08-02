import { ref } from 'vue'

const STORAGE_KEY = 'huaizhen-theme'
const VALID_THEMES = ['light', 'dark']

function getInitialTheme() {
  const saved = localStorage.getItem(STORAGE_KEY)
  if (saved && VALID_THEMES.includes(saved)) return saved
  // 无记录时跟随系统偏好
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    return 'dark'
  }
  return 'light'
}

const theme = ref(getInitialTheme())

function applyTheme(value) {
  const html = document.documentElement
  if (value === 'dark') {
    html.classList.add('dark')
  } else {
    html.classList.remove('dark')
  }
}

// 模块加载时立即应用,避免后续切换前状态不一致
applyTheme(theme.value)

export function useTheme() {
  const toggleTheme = () => {
    theme.value = theme.value === 'dark' ? 'light' : 'dark'
    localStorage.setItem(STORAGE_KEY, theme.value)
    applyTheme(theme.value)
  }

  const setTheme = (value) => {
    if (!VALID_THEMES.includes(value)) return
    theme.value = value
    localStorage.setItem(STORAGE_KEY, value)
    applyTheme(value)
  }

  return {
    theme,
    toggleTheme,
    setTheme
  }
}
