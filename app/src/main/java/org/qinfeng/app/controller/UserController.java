package org.qinfeng.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.app.common.Result;
import org.qinfeng.app.dto.PageQuery;
import org.qinfeng.app.entity.User;
import org.qinfeng.app.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 论坛用户管理控制器
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@RestController
@RequestMapping("/api/app/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(PageQuery query) {
        Page<User> page = new Page<>(query.getPage(), query.getSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(User::getUsername, query.getKeyword())
                    .or().like(User::getNickname, query.getKeyword()));
        }
        if (query.getStatus() != null) {
            wrapper.eq(User::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> result = userService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPasswordHash(null);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> addUser(@RequestBody User user) {
        User existUser = userService.findByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        if (StringUtils.hasText(user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        boolean success = userService.save(user);
        return success ? Result.success("创建用户成功") : Result.error("创建用户失败");
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<Void> updateUser(@RequestBody User user) {
        User exist = userService.getById(user.getId());
        if (exist == null) {
            return Result.error("用户不存在");
        }

        if (StringUtils.hasText(user.getUsername()) && !user.getUsername().equals(exist.getUsername())) {
            User dup = userService.findByUsername(user.getUsername());
            if (dup != null && !dup.getId().equals(user.getId())) {
                return Result.error("用户名已存在");
            }
            exist.setUsername(user.getUsername());
        }

        if (StringUtils.hasText(user.getNickname())) {
            exist.setNickname(user.getNickname());
        }
        if (user.getStatus() != null) {
            exist.setStatus(user.getStatus());
        }
        if (StringUtils.hasText(user.getPasswordHash())) {
            exist.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        boolean success = userService.updateById(exist);
        return success ? Result.success("更新用户成功") : Result.error("更新用户失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        boolean success = userService.removeById(id);
        return success ? Result.success("删除用户成功") : Result.error("删除用户失败");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的用户");
        }
        boolean success = userService.removeByIds(java.util.List.of(ids));
        return success ? Result.success("批量删除用户成功") : Result.error("删除用户失败");
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值不正确");
        }

        user.setStatus(status);
        boolean success = userService.updateById(user);
        return success
                ? Result.success(status == 1 ? "启用用户成功" : "禁用用户成功")
                : Result.error("更新状态失败");
    }
}
