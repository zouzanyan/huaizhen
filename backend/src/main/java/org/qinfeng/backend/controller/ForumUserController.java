package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.entity.User;
import org.qinfeng.backend.service.ForumUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 论坛用户管理控制器
 *
 * @author qinfeng
 * @since 2026-02-23
 */
@RestController
@RequestMapping("/api/forum/user")
@RequiredArgsConstructor
public class ForumUserController {

    private final ForumUserService forumUserService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Short role,
            @RequestParam(required = false) Short status) {

        Page<User> userPage = forumUserService.getUserList(page, size, keyword, role, status);

        Map<String, Object> data = Map.of(
                "list", userPage.getRecords(),
                "total", userPage.getTotal(),
                "page", userPage.getCurrent(),
                "size", userPage.getSize()
        );

        return Result.success(data);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> user = forumUserService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Short> body) {
        Short status = body.get("status");
        if (status == null) {
            return Result.error("状态值不能为空");
        }

        boolean success = forumUserService.updateUserStatus(id, status);
        return success ? Result.success("更新状态成功") : Result.error("更新状态失败");
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/{id}/role")
    public Result<Void> updateUserRole(@PathVariable Long id, @RequestBody Map<String, Short> body) {
        Short role = body.get("role");
        if (role == null) {
            return Result.error("角色值不能为空");
        }

        boolean success = forumUserService.updateUserRole(id, role);
        return success ? Result.success("更新角色成功") : Result.error("更新角色失败");
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean success = forumUserService.updateUser(user);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = forumUserService.getUserStats();
        return Result.success(stats);
    }

}
