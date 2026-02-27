import request from '@/utils/request'

const API_URL = '/api/forum/post'

/**
 * 论坛帖子管理 API 服务
 */
class ForumPostService {
  /**
   * 分页查询帖子列表
   * @param {Object} params 查询参数 { page, size, keyword, forumId, status }
   */
  getPostList(params) {
    return request({
      url: `${API_URL}/list`,
      method: 'get',
      params
    })
  }

  /**
   * 获取帖子详情
   * @param {Number} id 帖子ID
   */
  getPostById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  }

  /**
   * 删除帖子
   * @param {Number} id 帖子ID
   */
  deletePost(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }

  /**
   * 批量删除帖子
   * @param {Array} ids 帖子ID数组
   */
  batchDeletePosts(ids) {
    return request({
      url: `${API_URL}/batch`,
      method: 'delete',
      data: ids
    })
  }

  /**
   * 更新帖子状态
   * @param {Number} id 帖子ID
   * @param {Number} status 状态
   */
  updatePostStatus(id, status) {
    return request({
      url: `${API_URL}/${id}/status`,
      method: 'put',
      data: { status }
    })
  }

  /**
   * 获取帖子统计信息
   */
  getPostStats() {
    return request({
      url: `${API_URL}/stats`,
      method: 'get'
    })
  }

  /**
   * 创建帖子
   * @param {Object} data 帖子数据 { forumId, userId, title, content, contentType, status }
   */
  createPost(data) {
    return request({
      url: `${API_URL}`,
      method: 'post',
      data
    })
  }
}

export default new ForumPostService()
