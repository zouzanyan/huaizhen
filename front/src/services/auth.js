import axios from 'axios'

const API_URL = 'http://localhost:8080/api/auth'

class AuthService {
  login(username, password) {
    return axios.post(`${API_URL}/login`, {
      username,
      password
    }, {
      timeout: 15000, // 增加超时时长到 15 秒
      headers: {
        'Content-Type': 'application/json'
      }
    })
  }

  logout() {
    return axios.post(`${API_URL}/logout`, {}, {
      timeout: 5000,
      headers: {
        'Authorization': `Bearer ${this.getToken()}`
      }
    })
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
      console.log('isAuthenticated: false (no token)')
      return false
    }

    // 简单的 token 格式验证
    try {
      const parts = token.split('.')
      if (parts.length !== 3) {
        console.log('isAuthenticated: false (invalid token format)')
        return false
      }
      console.log('isAuthenticated: true')
      return true
    } catch (e) {
      console.error('isAuthenticated: false (error)', e)
      return false
    }
  }
}

export default new AuthService()