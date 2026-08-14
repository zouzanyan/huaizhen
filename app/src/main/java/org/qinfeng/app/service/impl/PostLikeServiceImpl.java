package org.qinfeng.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.app.entity.PostLike;
import org.qinfeng.app.mapper.PostLikeMapper;
import org.qinfeng.app.service.IPostLikeService;
import org.springframework.stereotype.Service;

/**
 * 帖子点赞服务实现类
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@Service
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements IPostLikeService {

    @Override
    public boolean like(Long postId, Long userId) {
        if (isLiked(postId, userId)) {
            return false;
        }
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        return save(postLike);
    }

    @Override
    public boolean unlike(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);
        return remove(wrapper);
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);
        return count(wrapper) > 0;
    }

    @Override
    public long countByPostId(Long postId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId);
        return count(wrapper);
    }
}
