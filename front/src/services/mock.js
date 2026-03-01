import axios from 'axios'

const API_URL = 'http://localhost:8080/api/mock'

class MockService {
  // 创建用户
  createUser(data) {
    return axios.post(`${API_URL}/user`, data, {
      timeout: 10000,
      headers: { 'Content-Type': 'application/json' }
    })
  }

  // 创建帖子
  createPost(data) {
    return axios.post(`${API_URL}/post`, data, {
      timeout: 10000,
      headers: { 'Content-Type': 'application/json' }
    })
  }

  // 创建评论
  createComment(data) {
    return axios.post(`${API_URL}/comment`, data, {
      timeout: 10000,
      headers: { 'Content-Type': 'application/json' }
    })
  }

  // 创建点赞
  createLike(postId, userId) {
    return axios.post(`${API_URL}/like`, null, {
      timeout: 10000,
      params: { postId, userId }
    })
  }

  // 创建收藏
  createFavorite(postId, userId) {
    return axios.post(`${API_URL}/favorite`, null, {
      timeout: 10000,
      params: { postId, userId }
    })
  }

  // 获取统计
  getStats() {
    return axios.get(`${API_URL}/stats`, {
      timeout: 10000
    })
  }
}

export default new MockService()
