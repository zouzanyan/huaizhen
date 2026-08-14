import service from '@/utils/request'

export function like(postId, userId) {
  return service.post('/app/like', null, { params: { postId, userId } })
}

export function unlike(postId, userId) {
  return service.delete('/app/like', { params: { postId, userId } })
}

export function checkLiked(postId, userId) {
  return service.get('/app/like/check', { params: { postId, userId } })
}

export function likeCount(postId) {
  return service.get(`/app/like/count/${postId}`)
}
