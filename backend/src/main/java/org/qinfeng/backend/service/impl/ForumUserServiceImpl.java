package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.entity.User;
import org.qinfeng.backend.mapper.UsersMapper;
import org.qinfeng.backend.service.ForumUserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 论坛用户服务实现
 *
 * @author qinfeng
 * @since 2026-02-23
 */
@Service
@RequiredArgsConstructor
public class ForumUserServiceImpl implements ForumUserService {

    private final UsersMapper usersMapper;

    @Override
    public Page<User> getUserList(Integer page, Integer size, String keyword, Short role, Short status) {
        Page<User> pageParam = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like("username", keyword)
                    .or()
                    .like("nickname", keyword)
                    .or()
                    .like("email", keyword)
            );
        }

        if (role != null) {
            queryWrapper.eq("role", role);
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("created_at");
        return usersMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public Map<String, Object> getUserById(Long id) {
        User user = usersMapper.selectById(id);
        if (user == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("email", user.getEmail());
        result.put("avatarUrl", user.getAvatarUrl());
        result.put("role", user.getRole());
        result.put("status", user.getStatus());
        result.put("createdAt", user.getCreatedAt());
        result.put("updatedAt", user.getUpdatedAt());

        return result;
    }

    @Override
    public boolean updateUserStatus(Long id, Short status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return usersMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateUserRole(Long id, Short role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return usersMapper.updateById(user) > 0;
    }

    @Override
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Long totalUsers = usersMapper.selectCount(queryWrapper);
        stats.put("totalUsers", totalUsers);

        // 按状态统计
        QueryWrapper<User> activeWrapper = new QueryWrapper<>();
        activeWrapper.eq("status", (short) 1);
        Long activeUsers = usersMapper.selectCount(activeWrapper);
        stats.put("activeUsers", activeUsers);

        QueryWrapper<User> bannedWrapper = new QueryWrapper<>();
        bannedWrapper.eq("status", (short) 0);
        Long bannedUsers = usersMapper.selectCount(bannedWrapper);
        stats.put("bannedUsers", bannedUsers);

        // 按角色统计
        QueryWrapper<User> adminWrapper = new QueryWrapper<>();
        adminWrapper.eq("role", (short) 1);
        Long adminUsers = usersMapper.selectCount(adminWrapper);
        stats.put("adminUsers", adminUsers);

        QueryWrapper<User> normalWrapper = new QueryWrapper<>();
        normalWrapper.eq("role", (short) 0);
        Long normalUsers = usersMapper.selectCount(normalWrapper);
        stats.put("normalUsers", normalUsers);

        return stats;
    }
}
