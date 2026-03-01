import request from '@/utils/request'

const API_URL = '/api/forum/comment'

/**
 * 论坛评论管理 API 服务
 */
class ForumCommentService {
  /**
   * 分页查询评论列表
   * @param {Object} params 查询参数 { page, size, keyword, postId, status }
   */
  getCommentList(params) {
    return request({
      url: `${API_URL}/list`,
      method: 'get',
      params
    })
  }

  /**
   * 获取评论详情
   * @param {Number} id 评论ID
   */
  getCommentById(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'get'
    })
  }

  /**
   * 删除评论
   * @param {Number} id 评论ID
   */
  deleteComment(id) {
    return request({
      url: `${API_URL}/${id}`,
      method: 'delete'
    })
  }

  /**
   * 批量删除评论
   * @param {Array} ids 评论ID数组
   */
  batchDeleteComments(ids) {
    return request({
      url: `${API_URL}/batch`,
      method: 'delete',
      data: ids
    })
  }

  /**
   * 更新评论状态
   * @param {Number} id 评论ID
   * @param {Number} status 状态 (0-隐藏, 1-显示)
   */
  updateCommentStatus(id, status) {
    return request({
      url: `${API_URL}/${id}/status`,
      method: 'put',
      data: { status }
    })
  }

  /**
   * 更新评论内容
   * @param {Number} id 评论ID
   * @param {String} content 评论内容
   */
  updateCommentContent(id, content) {
    return request({
      url: `${API_URL}/${id}/content`,
      method: 'put',
      data: { content }
    })
  }

  /**
   * 获取评论统计信息
   */
  getCommentStats() {
    return request({
      url: `${API_URL}/stats`,
      method: 'get'
    })
  }
}

export default new ForumCommentService()
