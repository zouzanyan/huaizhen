package org.qinfeng.backend.service;

import org.qinfeng.backend.entity.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色菜单服务类
 * </p>
 *
 * @author qinfeng
 * @since 2026-02-09
 */
public interface ISysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 根据角色ID获取菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 根据角色ID删除角色菜单关联
     *
     * @param roleId 角色ID
     */
    void deleteByRoleId(Long roleId);

    /**
     * 根据菜单ID删除角色菜单关联
     *
     * @param menuId 菜单ID
     */
    void deleteByMenuId(Long menuId);

    /**
     * 保存角色菜单关联
     *
     * @param roleId 角色ID
     * @param menuId 菜单ID
     */
    void saveRoleMenu(Long roleId, Long menuId);
}
