package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.entity.Forum;
import org.qinfeng.backend.service.ForumService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 论坛版块管理控制器
 *
 * @author qinfeng
 * @since 2026-02-23
 */
@RestController
@RequestMapping("/api/forum/board")
@RequiredArgsConstructor
public class ForumBoardController {

    private final ForumService forumService;

    /**
     * 分页查询版块列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getForumList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Short status) {

        Page<Forum> forumPage = forumService.getForumList(page, size, keyword, status);

        Map<String, Object> data = Map.of(
                "list", forumPage.getRecords(),
                "total", forumPage.getTotal(),
                "page", forumPage.getCurrent(),
                "size", forumPage.getSize()
        );

        return Result.success(data);
    }

    /**
     * 获取所有版块（不分页，用于下拉选择）
     */
    @GetMapping("/all")
    public Result<List<Forum>> getAllForums() {
        List<Forum> forums = forumService.getAllForums();
        return Result.success(forums);
    }

    /**
     * 获取版块详情
     */
    @GetMapping("/{id}")
    public Result<Forum> getForumById(@PathVariable Long id) {
        Forum forum = forumService.getForumById(id);
        if (forum == null) {
            return Result.error("版块不存在");
        }
        return Result.success(forum);
    }

    /**
     * 创建版块
     */
    @PostMapping
    public Result<Void> createForum(@RequestBody Forum forum) {
        boolean success = forumService.createForum(forum);
        return success ? Result.success("创建成功") : Result.error("创建失败");
    }

    /**
     * 更新版块
     */
    @PutMapping
    public Result<Void> updateForum(@RequestBody Forum forum) {
        if (forum.getId() == null) {
            return Result.error("版块ID不能为空");
        }

        boolean success = forumService.updateForum(forum);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除版块
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteForum(@PathVariable Long id) {
        boolean success = forumService.deleteForum(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取版块统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getForumStats() {
        Map<String, Object> stats = forumService.getForumStats();
        return Result.success(stats);
    }
}
