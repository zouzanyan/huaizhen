package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.entity.Post;

import java.util.Map;

/**
 * 帖子服务接口
 *
 * @author qinfeng
 * @since 2026-02-23
 */
public interface PostService {

    /**
     * 分页查询帖子列表
     */
    Page<Post> getPostList(Integer page, Integer size, String keyword, Long forumId, Short status);

    /**
     * 根据ID获取帖子详情
     */
    Map<String, Object> getPostById(Long id);

    /**
     * 删除帖子
     */
    boolean deletePost(Long id);

    /**
     * 批量删除帖子
     */
    boolean batchDeletePosts(Long[] ids);

    /**
     * 更新帖子状态
     */
    boolean updatePostStatus(Long id, Short status);

    /**
     * 获取帖子统计信息
     */
    Map<String, Object> getPostStats();
}
