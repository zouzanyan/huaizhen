package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.qinfeng.backend.entity.SysUserRole;
import org.qinfeng.backend.mapper.SysUserRoleMapper;
import org.qinfeng.backend.service.ISysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色服务实现类
 * </p>
 *
 * @author qinfeng
 * @since 2026-02-09
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getSysUserId, userId);
        wrapper.select(SysUserRole::getRoleId);
        List<SysUserRole> userRoles = list(wrapper);
        return userRoles.stream().map(SysUserRole::getRoleId).toList();
    }

    @Override
    public void deleteByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getSysUserId, userId);
        remove(wrapper);
    }

    @Override
    public void saveUserRole(Long userId, Long roleId) {
        SysUserRole userRole = new SysUserRole();
        userRole.setSysUserId(userId);
        userRole.setRoleId(roleId);
        save(userRole);
    }
}
