import request from '@/utils/request'

class AuthService {
  login(username, password) {
    return request.post('/api/auth/login', {
      username,
      password
    })
  }

  logout() {
    return request.post('/api/auth/logout')
  }

  getToken() {
    return localStorage.getItem('token')
  }

  setToken(token) {
    if (token) {
      localStorage.setItem('token', token)
    } else {
      console.warn('Token is empty, not saving to localStorage')
    }
  }

  removeToken() {
    localStorage.removeItem('token')
  }

  isAuthenticated() {
    const token = this.getToken()
    if (!token) {
      return false
    }

    try {
      const parts = token.split('.')
      if (parts.length !== 3) {
        return false
      }
      return true
    } catch (e) {
      return false
    }
  }

  getUsername() {
    const token = this.getToken()
    if (!token) {
      return null
    }

    try {
      const payload = token.split('.')[1]
      const decoded = JSON.parse(atob(payload))
      return decoded.username || null
    } catch (error) {
      console.error('解析用户名失败:', error)
      return null
    }
  }
}

export default new AuthService()
