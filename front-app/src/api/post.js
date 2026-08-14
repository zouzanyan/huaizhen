import service from '@/utils/request'

export function getPostList(params) {
  return service.get('/app/post/list', { params })
}

export function getPostById(id) {
  return service.get(`/app/post/${id}`)
}

export function createPost(data) {
  return service.post('/app/post', data)
}

export function deletePost(id) {
  return service.delete(`/app/post/${id}`)
}
