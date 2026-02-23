import request from '@/utils/request'

const API_URL = '/api/forum/user'

/**
 * 论坛用户管理 API 服务
 */
class ForumUserService {
  /**
   * 分页查询用户列表
   * @param {Object} params 查询参数 { page, size, keyword, role, status }
   */
  getUserList(params) {
    return request({
      url: `${API_URL}/list`,
      method: 'get',
      params
    })
  }

  /**
   * 获取用户详情
   * @param {Number} id 用户ID
   */
  getUserById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  }

  /**
   * 更新用户状态
   * @param {Number} id 用户ID
   * @param {Number} status 状态 (0-禁用, 1-启用)
   */
  updateUserStatus(id, status) {
    return request({
      url: `${API_URL}/${id}/status`,
      method: 'put',
      data: { status }
    })
  }

  /**
   * 更新用户角色
   * @param {Number} id 用户ID
   * @param {Number} role 角色 (0-普通用户, 1-管理员)
   */
  updateUserRole(id, role) {
    return request({
      url: `${API_URL}/${id}/role`,
      method: 'put',
      data: { role }
    })
  }

  /**
   * 获取用户统计信息
   */
  getUserStats() {
    return request({
      url: `${API_URL}/stats`,
      method: 'get'
    })
  }
}

export default new ForumUserService()
