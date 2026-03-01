package org.qinfeng.backend.service;

import org.qinfeng.backend.dto.MockCommentDTO;
import org.qinfeng.backend.dto.MockPostDTO;
import org.qinfeng.backend.dto.MockUserDTO;

/**
 * Mock数据生成服务接口
 *
 * @author qinfeng
 * @since 2026-03-01
 */
public interface MockDataService {

    /**
     * 创建单个用户
     */
    Long createUser(MockUserDTO userDTO);

    /**
     * 创建单个帖子
     */
    Long createPost(MockPostDTO postDTO);

    /**
     * 创建单条评论或回复
     */
    Long createComment(MockCommentDTO commentDTO);

    /**
     * 创建点赞
     */
    void createLike(Long postId, Long userId);

    /**
     * 创建收藏
     */
    void createFavorite(Long postId, Long userId);

    /**
     * 获取数据统计
     */
    Object getStats();
}
