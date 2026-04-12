import request from '@/utils/request'

const API_URL = '/api/kg/graph'

export default {
  getGraphData(projectId) {
    return request({
      url: `${API_URL}/data/${projectId}`,
      method: 'get'
    })
  },

  clearProjectData(projectId) {
    return request({
      url: `${API_URL}/clear/${projectId}`,
      method: 'delete'
    })
  }
}
