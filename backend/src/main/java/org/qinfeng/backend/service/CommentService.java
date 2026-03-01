package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.entity.Comment;

import java.util.Map;

/**
 * 评论服务接口
 *
 * @author qinfeng
 * @since 2026-03-01
 */
public interface CommentService {

    /**
     * 分页查询评论列表
     */
    Page<Map<String, Object>> getCommentList(Integer page, Integer size, String keyword, Long postId, Short status);

    /**
     * 根据ID获取评论详情
     */
    Map<String, Object> getCommentById(Long id);

    /**
     * 删除评论
     */
    boolean deleteComment(Long id);

    /**
     * 批量删除评论
     */
    boolean batchDeleteComments(Long[] ids);

    /**
     * 更新评论状态
     */
    boolean updateCommentStatus(Long id, Short status);

    /**
     * 更新评论内容
     */
    boolean updateCommentContent(Long id, String content);

    /**
     * 获取评论统计信息
     */
    Map<String, Object> getCommentStats();
}
