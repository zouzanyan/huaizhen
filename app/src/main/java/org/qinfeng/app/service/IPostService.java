package org.qinfeng.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qinfeng.app.entity.Post;

/**
 * 帖子服务类
 *
 * @author qinfeng
 * @since 2026-08-14
 */
public interface IPostService extends IService<Post> {

    /**
     * 浏览数 +1
     */
    void incrementViewCount(Long postId);

    /**
     * 评论数 +1
     */
    void incrementCommentCount(Long postId);

    /**
     * 评论数 -1
     */
    void decrementCommentCount(Long postId);
}
