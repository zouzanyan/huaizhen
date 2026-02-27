package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.entity.Post;
import org.qinfeng.backend.mapper.PostMapper;
import org.qinfeng.backend.service.PostService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子服务实现
 *
 * @author qinfeng
 * @since 2026-02-23
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    @Override
    public Page<Post> getPostList(Integer page, Integer size, String keyword, Long forumId, Short status) {
        // 先查询所有符合条件的数据（包含关联信息）
        List<Map<String, Object>> list = postMapper.getPostListWithDetails(keyword, forumId, status);

        // 手动分页
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, list.size());
        List<Map<String, Object>> pageList = list.subList(fromIndex, toIndex);

        // 构造分页结果
        Page<Map<String, Object>> pageParam = new Page<>(page, size, list.size());
        pageParam.setRecords(pageList);

        return (Page) pageParam;
    }

    @Override
    public Map<String, Object> getPostById(Long id) {
        return postMapper.getPostDetail(id);
    }

    @Override
    public boolean deletePost(Long id) {
        return postMapper.deleteById(id) > 0;
    }

    @Override
    public boolean batchDeletePosts(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return false;
        }
        for (Long id : ids) {
            postMapper.deleteById(id);
        }
        return true;
    }

    @Override
    public boolean updatePostStatus(Long id, Short status) {
        Post post = new Post();
        post.setId(id);
        post.setStatus(status);
        return postMapper.updateById(post) > 0;
    }

    @Override
    public Map<String, Object> getPostStats() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        Long totalPosts = postMapper.selectCount(queryWrapper);
        stats.put("totalPosts", totalPosts);

        // 按状态统计
        QueryWrapper<Post> activeWrapper = new QueryWrapper<>();
        activeWrapper.eq("status", (short) 1);
        Long activePosts = postMapper.selectCount(activeWrapper);
        stats.put("activePosts", activePosts);

        QueryWrapper<Post> deletedWrapper = new QueryWrapper<>();
        deletedWrapper.eq("status", (short) 0);
        Long deletedPosts = postMapper.selectCount(deletedWrapper);
        stats.put("deletedPosts", deletedPosts);

        return stats;
    }

    @Override
    public Long createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        if (post.getStatus() == null) {
            post.setStatus((short) 1);
        }
        if (post.getContentType() == null) {
            post.setContentType(0);
        }
        postMapper.insert(post);
        return post.getId();
    }
}
