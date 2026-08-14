import service from '@/utils/request'

export function getAllBoards() {
  return service.get('/app/board/all')
}

export function getBoardById(id) {
  return service.get(`/app/board/${id}`)
}
