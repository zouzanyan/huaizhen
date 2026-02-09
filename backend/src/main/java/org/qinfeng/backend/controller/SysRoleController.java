package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.dto.PageQuery;
import org.qinfeng.backend.dto.RoleDTO;
import org.qinfeng.backend.entity.SysPermission;
import org.qinfeng.backend.entity.SysRole;
import org.qinfeng.backend.service.ISysPermissionService;
import org.qinfeng.backend.service.ISysRoleMenuService;
import org.qinfeng.backend.service.ISysRolePermissionService;
import org.qinfeng.backend.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统角色管理控制器
 *
 * @author qinfeng
 * @since 2026-02-09
 */
@RestController
@RequestMapping("/api/sys/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    @Autowired
    private ISysRoleMenuService sysRoleMenuService;

    @Autowired
    private ISysPermissionService sysPermissionService;

    /**
     * 分页查询角色列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getRoleList(PageQuery query) {
        // 创建分页对象
        Page<SysRole> page = new Page<>(query.getPage(), query.getSize());

        // 构建查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        // 关键词查询（角色名称或编码）
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(SysRole::getName, query.getKeyword())
                    .or().like(SysRole::getCode, query.getKeyword()));
        }

        // 状态查询
        if (query.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, query.getStatus());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysRole::getCreatedAt);

        // 执行分页查询
        Page<SysRole> result = sysRoleService.page(page, wrapper);

        // 封装返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    /**
     * 获取所有角色列表（不分页，用于下拉选择）
     */
    @GetMapping("/all")
    public Result<List<SysRole>> getAllRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1);
        wrapper.orderByAsc(SysRole::getCreatedAt);
        List<SysRole> roles = sysRoleService.list(wrapper);
        return Result.success(roles);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public Result<SysRole> getRoleById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    @PostMapping
    public Result<Void> addRole(@RequestBody RoleDTO roleDTO) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getCode, roleDTO.getCode());
        SysRole existRole = sysRoleService.getOne(wrapper);
        if (existRole != null) {
            return Result.error("角色编码已存在");
        }

        // 创建角色
        SysRole role = new SysRole();
        role.setCode(roleDTO.getCode());
        role.setName(roleDTO.getName());
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);

        // 保存角色
        boolean success = sysRoleService.save(role);
        if (!success) {
            return Result.error("创建角色失败");
        }

        return Result.success("创建角色成功");
    }

    /**
     * 修改角色
     */
    @PutMapping
    public Result<Void> updateRole(@RequestBody RoleDTO roleDTO) {
        SysRole role = sysRoleService.getById(roleDTO.getId());
        if (role == null) {
            return Result.error("角色不存在");
        }

        // 检查角色编码是否与其他角色重复
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getCode, roleDTO.getCode());
        wrapper.ne(SysRole::getId, roleDTO.getId());
        SysRole existRole = sysRoleService.getOne(wrapper);
        if (existRole != null) {
            return Result.error("角色编码已存在");
        }

        // 更新角色信息
        role.setCode(roleDTO.getCode());
        role.setName(roleDTO.getName());
        if (roleDTO.getStatus() != null) {
            role.setStatus(roleDTO.getStatus());
        }

        boolean success = sysRoleService.updateById(role);
        if (!success) {
            return Result.error("更新角色失败");
        }

        return Result.success("更新角色成功");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }

        // 删除角色权限关联
        sysRolePermissionService.deleteByRoleId(id);

        // 删除角色菜单关联
        sysRoleMenuService.deleteByRoleId(id);

        // 删除角色
        boolean success = sysRoleService.removeById(id);
        if (!success) {
            return Result.error("删除角色失败");
        }

        return Result.success("删除角色成功");
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteRoles(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的角色");
        }

        // 删除角色权限关联和角色菜单关联
        for (Long id : ids) {
            sysRolePermissionService.deleteByRoleId(id);
            sysRoleMenuService.deleteByRoleId(id);
        }

        // 批量删除角色
        boolean success = sysRoleService.removeByIds(List.of(ids));
        if (!success) {
            return Result.error("删除角色失败");
        }

        return Result.success("批量删除角色成功");
    }

    /**
     * 启用/禁用角色
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateRoleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值不正确");
        }

        role.setStatus(status);
        boolean success = sysRoleService.updateById(role);
        if (!success) {
            return Result.error("更新状态失败");
        }

        return Result.success(status == 1 ? "启用角色成功" : "禁用角色成功");
    }

    /**
     * 获取角色权限列表
     */
    @GetMapping("/{roleId}/permissions")
    public Result<List<SysPermission>> getRolePermissions(@PathVariable Long roleId) {
        SysRole role = sysRoleService.getById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }

        List<Long> permissionIds = sysRolePermissionService.getPermissionIdsByRoleId(roleId);
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Result.success(List.of());
        }

        List<SysPermission> permissions = sysPermissionService.listByIds(permissionIds);
        return Result.success(permissions);
    }

    /**
     * 分配角色权限
     */
    @PostMapping("/{roleId}/permissions")
    public Result<Void> assignRolePermissions(@PathVariable Long roleId, @RequestBody Long[] permissionIds) {
        SysRole role = sysRoleService.getById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }

        // 删除原有权限关联
        sysRolePermissionService.deleteByRoleId(roleId);

        // 添加新权限关联
        if (permissionIds != null && permissionIds.length > 0) {
            for (Long permissionId : permissionIds) {
                sysRolePermissionService.saveRolePermission(roleId, permissionId);
            }
        }

        return Result.success("分配权限成功");
    }
}
