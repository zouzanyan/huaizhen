package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.entity.SysMenu;
import org.qinfeng.backend.security.JwtUser;
import org.qinfeng.backend.service.ISysMenuService;
import org.qinfeng.backend.service.ISysRoleMenuService;
import org.qinfeng.backend.service.ISysUserRoleService;
import org.qinfeng.backend.vo.MenuTreeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统菜单管理控制器
 *
 * @author qinfeng
 * @since 2026-02-09
 */
@RestController
@RequestMapping("/api/sys/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private ISysRoleMenuService sysRoleMenuService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    /**
     * 获取所有菜单列表（树形结构）
     */
    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> getMenuTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        wrapper.orderByAsc(SysMenu::getOrderNo);
        List<SysMenu> menus = sysMenuService.list(wrapper);
        List<MenuTreeVO> menuTree = buildMenuTree(menus, null);
        return Result.success(menuTree);
    }

    /**
     * 获取当前用户的菜单列表（树形结构）
     * 根据用户的角色权限过滤菜单
     */
    @GetMapping("/user/tree")
    public Result<List<MenuTreeVO>> getUserMenuTree() {
        // 获取当前登录用户
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = jwtUser.getUserId();

        // 获取用户的角色ID列表
        List<Long> roleIds = sysUserRoleService.getRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        // 获取这些角色对应的所有菜单ID
        Set<Long> menuIds = new HashSet<>();
        for (Long roleId : roleIds) {
            List<Long> roleIdMenuIds = sysRoleMenuService.getMenuIdsByRoleId(roleId);
            if (roleIdMenuIds != null) {
                menuIds.addAll(roleIdMenuIds);
            }
        }

        if (menuIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        // 获取菜单数据
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenu::getId, menuIds);
        wrapper.eq(SysMenu::getStatus, 1);
        wrapper.orderByAsc(SysMenu::getOrderNo);
        List<SysMenu> menus = sysMenuService.list(wrapper);

        // 构建菜单树
        List<MenuTreeVO> menuTree = buildMenuTree(menus, null);
        return Result.success(menuTree);
    }

    /**
     * 获取所有菜单列表（平铺结构）
     */
    @GetMapping("/list")
    public Result<List<SysMenu>> getMenuList() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        wrapper.orderByAsc(SysMenu::getOrderNo);
        List<SysMenu> menus = sysMenuService.list(wrapper);
        return Result.success(menus);
    }

    /**
     * 获取角色的菜单ID列表
     */
    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long roleId) {
        List<Long> menuIds = sysRoleMenuService.getMenuIdsByRoleId(roleId);
        return Result.success(menuIds);
    }

    /**
     * 分配角色菜单
     */
    @PostMapping("/{roleId}/menus")
    public Result<Void> assignRoleMenus(@PathVariable Long roleId, @RequestBody Long[] menuIds) {
        // 删除原有菜单关联
        sysRoleMenuService.deleteByRoleId(roleId);

        // 添加新菜单关联
        if (menuIds != null && menuIds.length > 0) {
            for (Long menuId : menuIds) {
                sysRoleMenuService.saveRoleMenu(roleId, menuId);
            }
        }

        return Result.success("分配菜单成功");
    }

    /**
     * 将 SysMenu 转换为 MenuTreeVO
     */
    private MenuTreeVO convertToVO(SysMenu menu) {
        MenuTreeVO vo = new MenuTreeVO();
        BeanUtils.copyProperties(menu, vo);
        return vo;
    }

    /**
     * 构建菜单树
     */
    private List<MenuTreeVO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        // 找到所有父菜单为 parentId 的菜单
        List<MenuTreeVO> rootMenus = menus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 为每个菜单设置子菜单
        for (MenuTreeVO menu : rootMenus) {
            List<MenuTreeVO> children = buildMenuTree(menus, menu.getId());
            menu.setChildren(children);
        }

        return rootMenus;
    }
}
