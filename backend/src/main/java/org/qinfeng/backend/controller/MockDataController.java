package org.qinfeng.backend.controller;

import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.dto.MockCommentDTO;
import org.qinfeng.backend.dto.MockPostDTO;
import org.qinfeng.backend.dto.MockUserDTO;
import org.qinfeng.backend.service.MockDataService;
import org.springframework.web.bind.annotation.*;

/**
 * Mock数据生成控制器 - 灵活创建单条数据
 *
 * @author qinfeng
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/api/mock")
@RequiredArgsConstructor
public class MockDataController {

    private final MockDataService mockDataService;

    /**
     * 创建单个用户
     */
    @PostMapping("/user")
    public Result<Long> createUser(@RequestBody MockUserDTO userDTO) {
        Long userId = mockDataService.createUser(userDTO);
        return Result.success("用户创建成功", userId);
    }

    /**
     * 创建单个帖子
     */
    @PostMapping("/post")
    public Result<Long> createPost(@RequestBody MockPostDTO postDTO) {
        Long postId = mockDataService.createPost(postDTO);
        return Result.success("帖子创建成功", postId);
    }

    /**
     * 创建单条评论或回复
     */
    @PostMapping("/comment")
    public Result<Long> createComment(@RequestBody MockCommentDTO commentDTO) {
        Long commentId = mockDataService.createComment(commentDTO);
        return Result.success("评论创建成功", commentId);
    }

    /**
     * 创建点赞
     */
    @PostMapping("/like")
    public Result<Void> createLike(@RequestParam String postId, @RequestParam String userId) {
        mockDataService.createLike(Long.parseLong(postId), Long.parseLong(userId));
        return Result.success("点赞成功");
    }

    /**
     * 创建收藏
     */
    @PostMapping("/favorite")
    public Result<Void> createFavorite(@RequestParam String postId, @RequestParam String userId) {
        mockDataService.createFavorite(Long.parseLong(postId), Long.parseLong(userId));
        return Result.success("收藏成功");
    }

    /**
     * 获取数据统计
     */
    @GetMapping("/stats")
    public Result<Object> getStats() {
        return Result.success(mockDataService.getStats());
    }
}
