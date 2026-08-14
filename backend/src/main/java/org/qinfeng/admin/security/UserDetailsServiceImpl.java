package org.qinfeng.admin.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.qinfeng.admin.entity.SysPermission;
import org.qinfeng.admin.entity.SysRole;
import org.qinfeng.admin.entity.SysRolePermission;
import org.qinfeng.admin.entity.SysUserRole;
import org.qinfeng.admin.service.ISysPermissionService;
import org.qinfeng.admin.service.ISysRolePermissionService;
import org.qinfeng.admin.service.ISysRoleService;
import org.qinfeng.admin.service.ISysUserRoleService;
import org.qinfeng.admin.service.ISysUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ISysUserService userService;
    private final ISysUserRoleService userRoleService;
    private final ISysRoleService roleService;
    private final ISysRolePermissionService rolePermissionService;
    private final ISysPermissionService permissionService;

    public UserDetailsServiceImpl(ISysUserService userService,
                                  ISysUserRoleService userRoleService,
                                  ISysRoleService roleService,
                                  ISysRolePermissionService rolePermissionService,
                                  ISysPermissionService permissionService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
        this.rolePermissionService = rolePermissionService;
        this.permissionService = permissionService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        org.qinfeng.admin.entity.SysUser user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 查询用户角色
        List<SysUserRole> userRoles = userRoleService.list(
            new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getSysUserId, user.getId())
        );

        List<String> roles = Collections.emptyList();
        List<String> permissions = Collections.emptyList();

        if (!userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

            // 查询角色信息
            List<SysRole> rolesList = roleService.listByIds(roleIds);
            roles = rolesList.stream()
                .map(SysRole::getCode)
                .collect(Collectors.toList());

            // 查询角色权限
            List<SysRolePermission> rolePermissions = rolePermissionService.list(
                new LambdaQueryWrapper<SysRolePermission>()
                    .in(SysRolePermission::getRoleId, roleIds)
            );

            if (!rolePermissions.isEmpty()) {
                List<Long> permissionIds = rolePermissions.stream()
                    .map(SysRolePermission::getPermissionId)
                    .distinct()
                    .collect(Collectors.toList());

                // 查询权限信息
                List<SysPermission> permissionsList = permissionService.listByIds(permissionIds);
                permissions = permissionsList.stream()
                    .map(SysPermission::getCode)
                    .collect(Collectors.toList());
            }
        }

        return new JwtUser(user, roles, permissions);
    }
}