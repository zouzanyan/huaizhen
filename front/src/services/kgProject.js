import request from '@/utils/request'

const API_URL = '/api/kg/project'

export default {
  list(params) {
    return request({
      url: `${API_URL}/list`,
      method: 'get',
      params
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
