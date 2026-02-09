package org.qinfeng.backend.service;

import org.qinfeng.backend.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qinfeng
 * @since 2026-02-09
 */
public interface ISysUserService extends IService<SysUser> {

    SysUser findByUsername(String username);
}
