package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.qinfeng.backend.entity.SysRoleMenu;
import org.qinfeng.backend.mapper.SysRoleMenuMapper;
import org.qinfeng.backend.service.ISysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色菜单服务实现类
 * </p>
 *
 * @author qinfeng
 * @since 2026-02-09
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        wrapper.select(SysRoleMenu::getMenuId);
        List<SysRoleMenu> roleMenus = list(wrapper);
        return roleMenus.stream().map(SysRoleMenu::getMenuId).toList();
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        remove(wrapper);
    }

    @Override
    public void deleteByMenuId(Long menuId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getMenuId, menuId);
        remove(wrapper);
    }

    @Override
    public void saveRoleMenu(Long roleId, Long menuId) {
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleId(roleId);
        roleMenu.setMenuId(menuId);
        save(roleMenu);
    }
}
