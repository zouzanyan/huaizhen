import request from '@/utils/request'

const API_URL = '/api/sys/prompt'
const CATEGORY_URL = '/api/sys/prompt-category'

class PromptService {
  getCategoryList(params) {
    return request({
      url: `${CATEGORY_URL}/list`,
      method: 'get',
      params
    })
  }

  getAllCategories() {
    return request({
      url: `${CATEGORY_URL}/all`,
      method: 'get'
    })
  }

  addCategory(data) {
    return request({
      url: CATEGORY_URL,
      method: 'post',
      data
    })
  }

  updateCategory(data) {
    return request({
      url: CATEGORY_URL,
      method: 'put',
      data
    })
  }

  deleteCategory(id) {
    return request({
      url: `${CATEGORY_URL}/${id}`,
      method: 'delete'
    })
  }

  getPromptList(params) {
    return request({
      url: `${API_URL}/list`,
      method: 'get',
      params
    })
  }

  getPromptById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  }

  addPrompt(data) {
    return request({
      url: API_URL,
      method: 'post',
      data
    })
  }

  updatePrompt(data) {
    return request({
      url: API_URL,
      method: 'put',
      data
    })
  }

  deletePrompt(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }

  batchDeletePrompts(ids) {
    return request({
      url: `${API_URL}/batch`,
      method: 'delete',
      data: ids
    })
  }
}

export default new PromptService()
