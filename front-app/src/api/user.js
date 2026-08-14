import service from '@/utils/request'

export function getUserList(params) {
  return service.get('/app/user/list', { params })
}

export function getUserById(id) {
  return service.get(`/app/user/${id}`)
}
