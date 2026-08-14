import service from '@/utils/request'

export function getCommentsByPost(postId) {
  return service.get(`/app/comment/post/${postId}`)
}

export function createComment(data) {
  return service.post('/app/comment', data)
}

export function deleteComment(id) {
  return service.delete(`/app/comment/${id}`)
}
