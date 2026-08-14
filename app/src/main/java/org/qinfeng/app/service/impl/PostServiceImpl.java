package org.qinfeng.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.app.entity.Post;
import org.qinfeng.app.mapper.PostMapper;
import org.qinfeng.app.service.IPostService;
import org.springframework.stereotype.Service;

/**
 * 帖子服务实现类
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {

    @Override
    public void incrementViewCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
                .setSql("view_count = view_count + 1");
        update(wrapper);
    }

    @Override
    public void incrementCommentCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
                .setSql("comment_count = comment_count + 1");
        update(wrapper);
    }

    @Override
    public void decrementCommentCount(Long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, postId)
                .setSql("comment_count = GREATEST(comment_count - 1, 0)");
        update(wrapper);
    }
}
