package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.entity.User;

import java.util.Map;

/**
 * 论坛用户服务接口
 *
 * @author qinfeng
 * @since 2026-02-23
 */
public interface ForumUserService {

    /**
     * 分页查询用户列表
     */
    Page<User> getUserList(Integer page, Integer size, String keyword, Short role, Short status);

    /**
     * 根据ID获取用户
     */
    Map<String, Object> getUserById(Long id);

    /**
     * 更新用户状态
     */
    boolean updateUserStatus(Long id, Short status);

    /**
     * 更新用户角色
     */
    boolean updateUserRole(Long id, Short role);

    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStats();
}
