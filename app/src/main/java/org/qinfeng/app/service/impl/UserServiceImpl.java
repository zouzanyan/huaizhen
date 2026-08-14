package org.qinfeng.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.app.entity.User;
import org.qinfeng.app.mapper.UserMapper;
import org.qinfeng.app.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * 论坛用户服务实现类
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return getOne(queryWrapper);
    }
}
