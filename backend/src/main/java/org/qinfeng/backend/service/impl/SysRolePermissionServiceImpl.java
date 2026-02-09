package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.qinfeng.backend.entity.SysRolePermission;
import org.qinfeng.backend.mapper.SysRolePermissionMapper;
import org.qinfeng.backend.service.ISysRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限服务实现类
 * </p>
 *
 * @author qinfeng
 * @since 2026-02-09
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements ISysRolePermissionService {

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        wrapper.select(SysRolePermission::getPermissionId);
        List<SysRolePermission> rolePermissions = list(wrapper);
        return rolePermissions.stream().map(SysRolePermission::getPermissionId).toList();
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        remove(wrapper);
    }

    @Override
    public void saveRolePermission(Long roleId, Long permissionId) {
        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermissionId(permissionId);
        save(rolePermission);
    }
}
