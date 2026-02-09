package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.dto.PageQuery;
import org.qinfeng.backend.dto.UserDTO;
import org.qinfeng.backend.entity.SysRole;
import org.qinfeng.backend.entity.SysUser;
import org.qinfeng.backend.service.ISysRoleService;
import org.qinfeng.backend.service.ISysUserRoleService;
import org.qinfeng.backend.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统用户管理控制器
 *
 * @author qinfeng
 * @since 2026-02-09
 */
@RestController
@RequestMapping("/api/sys/user")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(PageQuery query) {
        // 创建分页对象
        Page<SysUser> page = new Page<>(query.getPage(), query.getSize());

        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 关键词查询（用户名）
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(SysUser::getUsername, query.getKeyword());
        }

        // 状态查询
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreatedAt);

        // 执行分页查询
        Page<SysUser> result = sysUserService.page(page, wrapper);

        // 封装返回结果
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
    public Result<SysUser> getUserById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 隐藏密码
        user.setPasswordHash(null);

        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> addUser(@RequestBody UserDTO userDTO) {
        // 检查用户名是否已存在
        SysUser existUser = sysUserService.findByUsername(userDTO.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(userDTO.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : 1);

        // 保存用户
        boolean success = sysUserService.save(user);
        if (!success) {
            return Result.error("创建用户失败");
        }

        // 分配角色
        if (userDTO.getRoleIds() != null && userDTO.getRoleIds().length > 0) {
            for (Long roleId : userDTO.getRoleIds()) {
                sysUserRoleService.saveUserRole(user.getId(), roleId);
            }
        }

        return Result.success("创建用户成功");
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<Void> updateUser(@RequestBody UserDTO userDTO) {
        SysUser user = sysUserService.getById(userDTO.getId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 检查用户名是否与其他用户重复
        SysUser existUser = sysUserService.findByUsername(userDTO.getUsername());
        if (existUser != null && !existUser.getId().equals(user.getId())) {
            return Result.error("用户名已存在");
        }

        // 更新用户信息
        user.setUsername(userDTO.getUsername());
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }

        // 如果提供了新密码，则更新密码
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }

        boolean success = sysUserService.updateById(user);
        if (!success) {
            return Result.error("更新用户失败");
        }

        // 更新用户角色关联
        sysUserRoleService.deleteByUserId(user.getId());
        if (userDTO.getRoleIds() != null && userDTO.getRoleIds().length > 0) {
            for (Long roleId : userDTO.getRoleIds()) {
                sysUserRoleService.saveUserRole(user.getId(), roleId);
            }
        }

        return Result.success("更新用户成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 删除用户角色关联
        sysUserRoleService.deleteByUserId(id);

        // 删除用户
        boolean success = sysUserService.removeById(id);
        if (!success) {
            return Result.error("删除用户失败");
        }

        return Result.success("删除用户成功");
    }

    /**
     * 获取用户角色列表
     */
    @GetMapping("/{userId}/roles")
    public Result<List<SysRole>> getUserRoles(@PathVariable Long userId) {
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        List<Long> roleIds = sysUserRoleService.getRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Result.success(List.of());
        }

        List<SysRole> roles = sysRoleService.listByIds(roleIds);
        return Result.success(roles);
    }

    /**
     * 分配用户角色
     */
    @PostMapping("/{userId}/roles")
    public Result<Void> assignUserRoles(@PathVariable Long userId, @RequestBody Long[] roleIds) {
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 删除原有角色关联
        sysUserRoleService.deleteByUserId(userId);

        // 添加新角色关联
        if (roleIds != null && roleIds.length > 0) {
            for (Long roleId : roleIds) {
                sysUserRoleService.saveUserRole(userId, roleId);
            }
        }

        return Result.success("分配角色成功");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的用户");
        }

        // 删除用户角色关联
        for (Long id : ids) {
            sysUserRoleService.deleteByUserId(id);
        }

        // 批量删除用户
        boolean success = sysUserService.removeByIds(List.of(ids));
        if (!success) {
            return Result.error("删除用户失败");
        }

        return Result.success("批量删除用户成功");
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值不正确");
        }

        user.setStatus(status);
        boolean success = sysUserService.updateById(user);
        if (!success) {
            return Result.error("更新状态失败");
        }

        return Result.success(status == 1 ? "启用用户成功" : "禁用用户成功");
    }
}
