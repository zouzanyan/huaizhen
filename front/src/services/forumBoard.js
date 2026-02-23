import request from '@/utils/request'

const API_URL = '/api/forum/board'

/**
 * 论坛版块管理 API 服务
 */
class ForumBoardService {
  /**
   * 分页查询版块列表
   * @param {Object} params 查询参数 { page, size, keyword, status }
   */
  getBoardList(params) {
    return request({
      url: `${API_URL}/list`,
      method: 'get',
      params
    })
  }

  /**
   * 获取所有版块（不分页）
   */
  getAllBoards() {
    return request({
      url: `${API_URL}/all`,
      method: 'get'
    })
  }

  /**
   * 获取版块详情
   * @param {Number} id 版块ID
   */
  getBoardById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  }

  /**
   * 创建版块
   * @param {Object} data 版块数据 { name, description, parentId, status }
   */
  createBoard(data) {
    return request({
      url: API_URL,
      method: 'post',
      data
    })
  }

  /**
   * 更新版块
   * @param {Object} data 版块数据 { id, name, description, parentId, status }
   */
  updateBoard(data) {
    return request({
      url: API_URL,
      method: 'put',
      data
    })
  }

  /**
   * 删除版块
   * @param {Number} id 版块ID
   */
  deleteBoard(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }

  /**
   * 获取版块统计信息
   */
  getBoardStats() {
    return request({
      url: `${API_URL}/stats`,
      method: 'get'
    })
  }
}

export default new ForumBoardService()
