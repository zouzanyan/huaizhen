import request from '@/utils/request'

const API_URL = '/api/ai/api'

class AiApiService {
  getAiApiList(params) {
    return request({
      url: `${API_URL}/list`,
      method: 'get',
      params
    })
  }

  getAllAiApis() {
    return request({
      url: `${API_URL}/all`,
      method: 'get'
    })
  }

  getAiApiById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  }

  addAiApi(data) {
    return request({
      url: API_URL,
      method: 'post',
      data
    })
  }

  updateAiApi(data) {
    return request({
      url: API_URL,
      method: 'put',
      data
    })
  }

  deleteAiApi(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }

  batchDeleteAiApis(ids) {
    return request({
      url: `${API_URL}/batch`,
      method: 'delete',
      data: ids
    })
  }

  updateStatus(id, status) {
    return request({
      url: `${API_URL}/${id}/status`,
      method: 'put',
      data: { status }
    })
  }
}

export default new AiApiService()
