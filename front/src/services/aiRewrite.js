import request from '@/utils/request'

const API_URL = '/api/ai/rewrite'

class AiRewriteService {
  getRewriteList(params) {
    return request({
      url: `${API_URL}/list`,
      method: 'get',
      params
    })
  }

  getRewriteById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  }

  addRewrite(data) {
    return request({
      url: API_URL,
      method: 'post',
      data
    })
  }

  updateRewrite(data) {
    return request({
      url: API_URL,
      method: 'put',
      data
    })
  }

  deleteRewrite(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }

  batchDeleteRewrites(ids) {
    return request({
      url: `${API_URL}/batch`,
      method: 'delete',
      data: ids
    })
  }

  executeRewrite(id) {
    return request({
      url: `${API_URL}/${id}/execute`,
      method: 'post'
    })
  }

  executeAndSave(data) {
    return request({
      url: `${API_URL}/execute`,
      method: 'post',
      data
    })
  }
}

export default new AiRewriteService()
