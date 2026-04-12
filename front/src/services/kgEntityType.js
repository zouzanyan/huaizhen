import request from '@/utils/request'

const API_URL = '/api/kg/entity-type'

export default {
  listByProjectId(projectId) {
    return request({
      url: `${API_URL}/list/${projectId}`,
      method: 'get'
    })
  },

  getById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  },

  create(data) {
    return request({
      url: API_URL,
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: API_URL,
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }
}
