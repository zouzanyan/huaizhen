package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.entity.Post;
import org.qinfeng.backend.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 论坛帖子管理控制器
 *
 * @author qinfeng
 * @since 2026-02-23
 */
@RestController
@RequestMapping("/api/forum/post")
@RequiredArgsConstructor
public class ForumPostController {

    private final PostService postService;

    /**
     * 分页查询帖子列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long forumId,
            @RequestParam(required = false) Short status) {

        Page<Post> postPage = postService.getPostList(page, size, keyword, forumId, status);

        Map<String, Object> data = Map.of(
                "list", postPage.getRecords(),
                "total", postPage.getTotal(),
                "page", postPage.getCurrent(),
                "size", postPage.getSize()
        );

        return Result.success(data);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getPostDetail(@PathVariable Long id) {
        Map<String, Object> post = postService.getPostById(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        return Result.success(post);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        boolean success = postService.deletePost(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除帖子
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeletePosts(@RequestBody Long[] ids) {
        boolean success = postService.batchDeletePosts(ids);
        return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
    }

    /**
     * 更新帖子状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updatePostStatus(@PathVariable Long id, @RequestBody Map<String, Short> body) {
        Short status = body.get("status");
        if (status == null) {
            return Result.error("状态值不能为空");
        }

        boolean success = postService.updatePostStatus(id, status);
        return success ? Result.success("更新状态成功") : Result.error("更新状态失败");
    }

    /**
     * 获取帖子统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getPostStats() {
        Map<String, Object> stats = postService.getPostStats();
        return Result.success(stats);
    }

    /**
     * 创建帖子
     */
    @PostMapping
    public Result<Long> createPost(@RequestBody Post post) {
        Long postId = postService.createPost(post);
        return Result.success("创建成功", postId);
    }

    /**
     * 更新帖子
     */
    @PutMapping
    public Result<Void> updatePost(@RequestBody Post post) {
        if (post.getId() == null) {
            return Result.error("帖子ID不能为空");
        }

        boolean success = postService.updatePost(post);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
}
