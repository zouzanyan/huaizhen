package org.qinfeng.backend.service;

import org.qinfeng.backend.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户角色服务类
 * </p>
 *
 * @author qinfeng
 * @since 2026-02-09
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户ID获取角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);

    /**
     * 保存用户角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void saveUserRole(Long userId, Long roleId);
}
