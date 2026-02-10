import request from '@/utils/request'

const API_URL = '/api/sys/menu'

/**
 * 菜单管理 API 服务
 */
class MenuService {
  /**
   * 获取菜单树
   */
  getMenuTree() {
    return request({
      url: `${API_URL}/tree`,
      method: 'get'
    })
  }

  /**
   * 获取当前用户的菜单树
   */
  getUserMenuTree() {
    return request({
      url: `${API_URL}/user/tree`,
      method: 'get'
    })
  }

  /**
   * 获取所有菜单列表（平铺）
   */
  getMenuList() {
    return request({
      url: `${API_URL}/list`,
      method: 'get'
    })
  }

  /**
   * 根据ID获取菜单详情
   * @param {Number} id 菜单ID
   */
  getMenuById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  }

  /**
   * 新增菜单
   * @param {Object} data 菜单数据
   */
  addMenu(data) {
    return request({
      url: API_URL,
      method: 'post',
      data
    })
  }

  /**
   * 更新菜单
   * @param {Object} data 菜单数据
   */
  updateMenu(data) {
    return request({
      url: API_URL,
      method: 'put',
      data
    })
  }

  /**
   * 删除菜单
   * @param {Number} id 菜单ID
   */
  deleteMenu(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }

  /**
   * 获取角色的菜单ID列表
   * @param {Number} roleId 角色ID
   */
  getRoleMenus(roleId) {
    return request({
      url: `${API_URL}/${roleId}/menus`,
      method: 'get'
    })
  }

  /**
   * 分配角色菜单
   * @param {Number} roleId 角色ID
   * @param {Array} menuIds 菜单ID数组
   */
  assignRoleMenus(roleId, menuIds) {
    return request({
      url: `${API_URL}/${roleId}/menus`,
      method: 'post',
      data: menuIds
    })
  }
}

export default new MenuService()
