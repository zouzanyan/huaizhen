import request from '@/utils/request'

const API_URL = '/api/sys/user'

/**
 * 用户管理 API 服务
 */
class UserService {
  /**
   * 分页查询用户列表
   * @param {Object} params 查询参数 { page, size, keyword, status }
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
   * 新增用户
   * @param {Object} data 用户数据 { username, password, status, roleIds }
   */
  addUser(data) {
    return request({
      url: API_URL,
      method: 'post',
      data
    })
  }

  /**
   * 修改用户
   * @param {Object} data 用户数据 { id, username, password, status, roleIds }
   */
  updateUser(data) {
    return request({
      url: API_URL,
      method: 'put',
      data
    })
  }

  /**
   * 删除用户
   * @param {Number} id 用户ID
   */
  deleteUser(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }

  /**
   * 批量删除用户
   * @param {Array} ids 用户ID数组
   */
  batchDeleteUsers(ids) {
    return request({
      url: `${API_URL}/batch`,
      method: 'delete',
      data: ids
    })
  }

  /**
   * 获取用户角色列表
   * @param {Number} userId 用户ID
   */
  getUserRoles(userId) {
    return request({
      url: `${API_URL}/${userId}/roles`,
      method: 'get'
    })
  }

  /**
   * 分配用户角色
   * @param {Number} userId 用户ID
   * @param {Array} roleIds 角色ID数组
   */
  assignUserRoles(userId, roleIds) {
    return request({
      url: `${API_URL}/${userId}/roles`,
      method: 'post',
      data: roleIds
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
}

export default new UserService()
