import request from '@/utils/request'

export default {
  getStats() {
    return request.get('/api/dashboard/stats')
  },
  getRecentProjects() {
    return request.get('/api/dashboard/recentProjects')
  },
  getRecentCorpus() {
    return request.get('/api/dashboard/recentCorpus')
  }
}
