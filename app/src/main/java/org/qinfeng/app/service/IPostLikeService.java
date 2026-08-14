package org.qinfeng.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qinfeng.app.entity.PostLike;

/**
 * 帖子点赞服务类
 *
 * @author qinfeng
 * @since 2026-08-14
 */
public interface IPostLikeService extends IService<PostLike> {

    /**
     * 点赞
     *
     * @return true 表示点赞成功，false 表示已点赞过
     */
    boolean like(Long postId, Long userId);

    /**
     * 取消点赞
     *
     * @return true 表示取消成功，false 表示未点赞过
     */
    boolean unlike(Long postId, Long userId);

    /**
     * 是否已点赞
     */
    boolean isLiked(Long postId, Long userId);

    /**
     * 统计帖子点赞数
     */
    long countByPostId(Long postId);
}
