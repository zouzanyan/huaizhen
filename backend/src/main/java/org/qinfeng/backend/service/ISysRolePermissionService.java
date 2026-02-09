package org.qinfeng.backend.service;

import org.qinfeng.backend.entity.SysRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色权限服务类
 * </p>
 *
 * @author qinfeng
 * @since 2026-02-09
 */
public interface ISysRolePermissionService extends IService<SysRolePermission> {

    /**
     * 根据角色ID获取权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);

    /**
     * 根据角色ID删除角色权限关联
     *
     * @param roleId 角色ID
     */
    void deleteByRoleId(Long roleId);

    /**
     * 保存角色权限关联
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     */
    void saveRolePermission(Long roleId, Long permissionId);
}
