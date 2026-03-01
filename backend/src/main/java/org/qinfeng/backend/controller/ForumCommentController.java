package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 论坛评论管理控制器
 *
 * @author qinfeng
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/api/forum/comment")
@RequiredArgsConstructor
public class ForumCommentController {

    private final CommentService commentService;

    /**
     * 分页查询评论列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getCommentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Short status) {

        Page<Map<String, Object>> commentPage = commentService.getCommentList(page, size, keyword, postId, status);

        Map<String, Object> data = Map.of(
                "list", commentPage.getRecords(),
                "total", commentPage.getTotal(),
                "page", commentPage.getCurrent(),
                "size", commentPage.getSize()
        );

        return Result.success(data);
    }

    /**
     * 获取评论详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getCommentById(@PathVariable Long id) {
        Map<String, Object> comment = commentService.getCommentById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        return Result.success(comment);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        boolean success = commentService.deleteComment(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除评论
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteComments(@RequestBody Long[] ids) {
        boolean success = commentService.batchDeleteComments(ids);
        return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
    }

    /**
     * 更新评论状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateCommentStatus(@PathVariable Long id, @RequestBody Map<String, Short> body) {
        Short status = body.get("status");
        if (status == null) {
            return Result.error("状态值不能为空");
        }

        boolean success = commentService.updateCommentStatus(id, status);
        return success ? Result.success("更新状态成功") : Result.error("更新状态失败");
    }

    /**
     * 更新评论内容
     */
    @PutMapping("/{id}/content")
    public Result<Void> updateCommentContent(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.isEmpty()) {
            return Result.error("评论内容不能为空");
        }

        boolean success = commentService.updateCommentContent(id, content);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 获取评论统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCommentStats() {
        Map<String, Object> stats = commentService.getCommentStats();
        return Result.success(stats);
    }
}
