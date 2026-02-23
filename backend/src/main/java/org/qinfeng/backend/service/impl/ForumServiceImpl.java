package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.entity.Forum;
import org.qinfeng.backend.entity.Post;
import org.qinfeng.backend.mapper.ForumMapper;
import org.qinfeng.backend.mapper.PostMapper;
import org.qinfeng.backend.service.ForumService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版块服务实现
 *
 * @author qinfeng
 * @since 2026-02-23
 */
@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

    private final ForumMapper forumMapper;
    private final PostMapper postMapper;

    @Override
    public Page<Forum> getForumList(Integer page, Integer size, String keyword, Short status) {
        Page<Forum> pageParam = new Page<>(page, size);
        QueryWrapper<Forum> queryWrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like("name", keyword)
                    .or()
                    .like("description", keyword)
            );
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByAsc("parent_id").orderByDesc("created_at");
        return forumMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public List<Forum> getAllForums() {
        QueryWrapper<Forum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", (short) 1);
        queryWrapper.orderByAsc("parent_id").orderByAsc("id");
        return forumMapper.selectList(queryWrapper);
    }

    @Override
    public Forum getForumById(Long id) {
        return forumMapper.selectById(id);
    }

    @Override
    public boolean createForum(Forum forum) {
        return forumMapper.insert(forum) > 0;
    }

    @Override
    public boolean updateForum(Forum forum) {
        return forumMapper.updateById(forum) > 0;
    }

    @Override
    public boolean deleteForum(Long id) {
        return forumMapper.deleteById(id) > 0;
    }

    @Override
    public Map<String, Object> getForumStats() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<Forum> queryWrapper = new QueryWrapper<>();
        Long totalForums = forumMapper.selectCount(queryWrapper);
        stats.put("totalForums", totalForums);

        // 按状态统计
        QueryWrapper<Forum> activeWrapper = new QueryWrapper<>();
        activeWrapper.eq("status", (short) 1);
        Long activeForums = forumMapper.selectCount(activeWrapper);
        stats.put("activeForums", activeForums);

        // 统计每个版块的帖子数（这里简化为总帖子数）
        QueryWrapper<Post> postWrapper = new QueryWrapper<>();
        Long totalPosts = postMapper.selectCount(postWrapper);
        stats.put("totalPosts", totalPosts);

        return stats;
    }
}
