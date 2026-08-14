package org.qinfeng.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.app.common.Result;
import org.qinfeng.app.dto.PageQuery;
import org.qinfeng.app.entity.Post;
import org.qinfeng.app.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子管理控制器
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@RestController
@RequestMapping("/api/app/post")
public class PostController {

    @Autowired
    private IPostService postService;

    /**
     * 分页查询帖子列表
     *
     * @param boardId 可选，按板块筛选
     * @param userId  可选，按作者筛选
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getPostList(PageQuery query,
                                                   @RequestParam(required = false) Long boardId,
                                                   @RequestParam(required = false) Long userId) {
        Page<Post> page = new Page<>(query.getPage(), query.getSize());

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Post::getTitle, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Post::getStatus, query.getStatus());
        }
        if (boardId != null) {
            wrapper.eq(Post::getBoardId, boardId);
        }
        if (userId != null) {
            wrapper.eq(Post::getUserId, userId);
        }
        wrapper.orderByDesc(Post::getCreatedAt);

        Page<Post> result = postService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    /**
     * 获取帖子详情（浏览数 +1）
     */
    @GetMapping("/{id}")
    public Result<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getById(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        postService.incrementViewCount(id);
        post.setViewCount(post.getViewCount() == null ? 1 : post.getViewCount() + 1);
        return Result.success(post);
    }

    /**
     * 新增帖子
     */
    @PostMapping
    public Result<Void> addPost(@RequestBody Post post) {
        if (post.getStatus() == null) {
            post.setStatus(1);
        }
        boolean success = postService.save(post);
        return success ? Result.success("发布帖子成功") : Result.error("发布帖子失败");
    }

    /**
     * 修改帖子
     */
    @PutMapping
    public Result<Void> updatePost(@RequestBody Post post) {
        Post exist = postService.getById(post.getId());
        if (exist == null) {
            return Result.error("帖子不存在");
        }

        if (StringUtils.hasText(post.getTitle())) {
            exist.setTitle(post.getTitle());
        }
        if (post.getContent() != null) {
            exist.setContent(post.getContent());
        }
        if (post.getBoardId() != null) {
            exist.setBoardId(post.getBoardId());
        }
        if (post.getStatus() != null) {
            exist.setStatus(post.getStatus());
        }

        boolean success = postService.updateById(exist);
        return success ? Result.success("更新帖子成功") : Result.error("更新帖子失败");
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        Post post = postService.getById(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        boolean success = postService.removeById(id);
        return success ? Result.success("删除帖子成功") : Result.error("删除帖子失败");
    }

    /**
     * 批量删除帖子
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeletePosts(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的帖子");
        }
        boolean success = postService.removeByIds(List.of(ids));
        return success ? Result.success("批量删除帖子成功") : Result.error("删除帖子失败");
    }

    /**
     * 启用/禁用帖子
     */
    @PutMapping("/{id}/status")
    public Result<Void> updatePostStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Post post = postService.getById(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值不正确");
        }

        post.setStatus(status);
        boolean success = postService.updateById(post);
        return success
                ? Result.success(status == 1 ? "启用帖子成功" : "禁用帖子成功")
                : Result.error("更新状态失败");
    }
}
