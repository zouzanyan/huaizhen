import service from '@/utils/request'

export function login(data) {
  return service.post('/app/auth/login', data)
}

export function register(data) {
  return service.post('/app/auth/register', data)
}
