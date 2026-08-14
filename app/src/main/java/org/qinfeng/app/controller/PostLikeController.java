package org.qinfeng.app.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import org.qinfeng.app.common.Result;
import org.qinfeng.app.entity.Post;
import org.qinfeng.app.service.IPostLikeService;
import org.qinfeng.app.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 帖子点赞控制器
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@RestController
@RequestMapping("/api/app/like")
public class PostLikeController {

    @Autowired
    private IPostLikeService postLikeService;

    @Autowired
    private IPostService postService;

    /**
     * 点赞
     */
    @PostMapping
    public Result<Void> like(@RequestParam Long postId, @RequestParam Long userId) {
        boolean success = postLikeService.like(postId, userId);
        if (success) {
            // 帖子点赞数 +1
            LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Post::getId, postId)
                    .setSql("like_count = like_count + 1");
            postService.update(wrapper);
            return Result.success("点赞成功");
        }
        return Result.error("已点赞过");
    }

    /**
     * 取消点赞
     */
    @DeleteMapping
    public Result<Void> unlike(@RequestParam Long postId, @RequestParam Long userId) {
        boolean success = postLikeService.unlike(postId, userId);
        if (success) {
            // 帖子点赞数 -1
            LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Post::getId, postId)
                    .setSql("like_count = GREATEST(like_count - 1, 0)");
            postService.update(wrapper);
            return Result.success("取消点赞成功");
        }
        return Result.error("未点赞过");
    }

    /**
     * 是否已点赞
     */
    @GetMapping("/check")
    public Result<Map<String, Object>> checkLiked(@RequestParam Long postId, @RequestParam Long userId) {
        boolean liked = postLikeService.isLiked(postId, userId);
        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        return Result.success(data);
    }

    /**
     * 帖子点赞数
     */
    @GetMapping("/count/{postId}")
    public Result<Map<String, Object>> likeCount(@PathVariable Long postId) {
        long count = postLikeService.countByPostId(postId);
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }
}
