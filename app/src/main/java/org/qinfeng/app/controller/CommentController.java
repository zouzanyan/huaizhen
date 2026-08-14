package org.qinfeng.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.app.common.Result;
import org.qinfeng.app.dto.PageQuery;
import org.qinfeng.app.entity.Comment;
import org.qinfeng.app.entity.User;
import org.qinfeng.app.service.ICommentService;
import org.qinfeng.app.service.IPostService;
import org.qinfeng.app.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论管理控制器
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@RestController
@RequestMapping("/api/app/comment")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IPostService postService;

    @Autowired
    private IUserService userService;

    /**
     * 分页查询评论列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getCommentList(PageQuery query,
                                                      @RequestParam(required = false) Long postId,
                                                      @RequestParam(required = false) Long userId) {
        Page<Comment> page = new Page<>(query.getPage(), query.getSize());

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (query.getStatus() != null) {
            wrapper.eq(Comment::getStatus, query.getStatus());
        }
        if (postId != null) {
            wrapper.eq(Comment::getPostId, postId);
        }
        if (userId != null) {
            wrapper.eq(Comment::getUserId, userId);
        }
        wrapper.orderByDesc(Comment::getCreatedAt);

        Page<Comment> result = commentService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    /**
     * 查询某帖子的评论列表（不分页）
     */
    @GetMapping("/post/{postId}")
    public Result<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 1)
                .orderByAsc(Comment::getCreatedAt);
        List<Comment> comments = commentService.list(wrapper);

        if (!comments.isEmpty()) {
            Set<Long> userIds = comments.stream()
                    .map(Comment::getUserId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (!userIds.isEmpty()) {
                Map<Long, String> nicknameMap = userService.listByIds(userIds).stream()
                        .collect(Collectors.toMap(
                                User::getId,
                                u -> u.getNickname() != null ? u.getNickname() : u.getUsername(),
                                (a, b) -> a));
                comments.forEach(c -> c.setNickname(nicknameMap.get(c.getUserId())));
            }
        }

        return Result.success(comments);
    }

    /**
     * 获取评论详情
     */
    @GetMapping("/{id}")
    public Result<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        return Result.success(comment);
    }

    /**
     * 新增评论（同时帖子评论数 +1）
     */
    @PostMapping
    public Result<Void> addComment(@RequestBody Comment comment) {
        if (comment.getStatus() == null) {
            comment.setStatus(1);
        }
        boolean success = commentService.save(comment);
        if (success && comment.getPostId() != null) {
            postService.incrementCommentCount(comment.getPostId());
        }
        return success ? Result.success("发表评论成功") : Result.error("发表评论失败");
    }

    /**
     * 删除评论（同时帖子评论数 -1）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        boolean success = commentService.removeById(id);
        if (success && comment.getPostId() != null) {
            postService.decrementCommentCount(comment.getPostId());
        }
        return success ? Result.success("删除评论成功") : Result.error("删除评论失败");
    }

    /**
     * 批量删除评论
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteComments(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的评论");
        }
        boolean success = commentService.removeByIds(List.of(ids));
        return success ? Result.success("批量删除评论成功") : Result.error("删除评论失败");
    }

    /**
     * 启用/禁用评论
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateCommentStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值不正确");
        }

        comment.setStatus(status);
        boolean success = commentService.updateById(comment);
        return success
                ? Result.success(status == 1 ? "启用评论成功" : "禁用评论成功")
                : Result.error("更新状态失败");
    }
}
