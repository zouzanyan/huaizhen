package org.qinfeng.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qinfeng.app.entity.User;

/**
 * 论坛用户服务类
 *
 * @author qinfeng
 * @since 2026-08-14
 */
public interface IUserService extends IService<User> {

    User findByUsername(String username);
}
