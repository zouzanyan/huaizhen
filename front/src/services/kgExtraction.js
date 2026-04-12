import request from '@/utils/request'

const API_URL = '/api/kg/extraction'

export default {
  extractByLlm(data) {
    return request({
      url: `${API_URL}/llm`,
      method: 'post',
      data
    })
  },

  previewExtraction(data) {
    return request({
      url: `${API_URL}/llm/preview`,
      method: 'post',
      data
    })
  }
}
