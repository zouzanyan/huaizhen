import request from '@/utils/request'

const API_URL = '/api/sys/role'

/**
 * 角色管理 API 服务
 */
class RoleService {
  /**
   * 分页查询角色列表
   * @param {Object} params 查询参数 { page, size, keyword, status }
   */
  getRoleList(params) {
    return request({
      url: `${API_URL}/list`,
      method: 'get',
      params
    })
  }

  /**
   * 获取所有角色列表（不分页，用于下拉选择）
   */
  getAllRoles() {
    return request({
      url: `${API_URL}/all`,
      method: 'get'
    })
  }

  /**
   * 获取角色详情
   * @param {Number} id 角色ID
   */
  getRoleById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  }

  /**
   * 新增角色
   * @param {Object} data 角色数据 { code, name, status }
   */
  addRole(data) {
    return request({
      url: API_URL,
      method: 'post',
      data
    })
  }

  /**
   * 修改角色
   * @param {Object} data 角色数据 { id, code, name, status }
   */
  updateRole(data) {
    return request({
      url: API_URL,
      method: 'put',
      data
    })
  }

  /**
   * 删除角色
   * @param {Number} id 角色ID
   */
  deleteRole(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }

  /**
   * 批量删除角色
   * @param {Array} ids 角色ID数组
   */
  batchDeleteRoles(ids) {
    return request({
      url: `${API_URL}/batch`,
      method: 'delete',
      data: ids
    })
  }

  /**
   * 更新角色状态
   * @param {Number} id 角色ID
   * @param {Number} status 状态 (0-禁用, 1-启用)
   */
  updateRoleStatus(id, status) {
    return request({
      url: `${API_URL}/${id}/status`,
      method: 'put',
      data: { status }
    })
  }

  /**
   * 获取角色权限列表
   * @param {Number} roleId 角色ID
   */
  getRolePermissions(roleId) {
    return request({
      url: `${API_URL}/${roleId}/permissions`,
      method: 'get'
    })
  }

  /**
   * 分配角色权限
   * @param {Number} roleId 角色ID
   * @param {Array} permissionIds 权限ID数组
   */
  assignRolePermissions(roleId, permissionIds) {
    return request({
      url: `${API_URL}/${roleId}/permissions`,
      method: 'post',
      data: permissionIds
    })
  }
}

export default new RoleService()
