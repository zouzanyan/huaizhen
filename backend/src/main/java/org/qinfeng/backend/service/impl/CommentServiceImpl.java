package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.entity.Comment;
import org.qinfeng.backend.mapper.CommentMapper;
import org.qinfeng.backend.mapper.PostMapper;
import org.qinfeng.backend.mapper.UsersMapper;
import org.qinfeng.backend.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 评论服务实现
 *
 * @author qinfeng
 * @since 2026-03-01
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UsersMapper usersMapper;
    private final PostMapper postMapper;

    @Override
    public Page<Map<String, Object>> getCommentList(Integer page, Integer size, String keyword, Long postId, Short status) {
        Page<Comment> pageParam = new Page<>(page, size);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("content", keyword);
        }

        if (postId != null) {
            queryWrapper.eq("post_id", postId);
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("created_at");
        Page<Comment> commentPage = commentMapper.selectPage(pageParam, queryWrapper);

        // 转换为带用户和帖子信息的列表
        Page<Map<String, Object>> resultPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();

        for (Comment comment : commentPage.getRecords()) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", comment.getId());
            record.put("postId", comment.getPostId());
            record.put("userId", comment.getUserId());
            record.put("parentId", comment.getParentId());
            record.put("content", comment.getContent());
            record.put("status", comment.getStatus());
            record.put("createdAt", comment.getCreatedAt());
            record.put("updatedAt", comment.getUpdatedAt());

            // 获取用户信息
            if (comment.getUserId() != null) {
                var user = usersMapper.selectById(comment.getUserId());
                if (user != null) {
                    record.put("authorName", user.getNickname());
                    record.put("authorAvatar", user.getAvatarUrl());
                }
            }

            // 获取帖子标题
            if (comment.getPostId() != null) {
                var post = postMapper.selectById(comment.getPostId());
                if (post != null) {
                    record.put("postTitle", post.getTitle());
                }
            }

            records.add(record);
        }

        resultPage.setRecords(records);
        return resultPage;
    }

    @Override
    public Map<String, Object> getCommentById(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", comment.getId());
        result.put("postId", comment.getPostId());
        result.put("userId", comment.getUserId());
        result.put("parentId", comment.getParentId());
        result.put("content", comment.getContent());
        result.put("status", comment.getStatus());
        result.put("createdAt", comment.getCreatedAt());
        result.put("updatedAt", comment.getUpdatedAt());

        // 获取用户信息
        if (comment.getUserId() != null) {
            var user = usersMapper.selectById(comment.getUserId());
            if (user != null) {
                result.put("authorName", user.getNickname());
                result.put("authorAvatar", user.getAvatarUrl());
            }
        }

        // 获取帖子标题
        if (comment.getPostId() != null) {
            var post = postMapper.selectById(comment.getPostId());
            if (post != null) {
                result.put("postTitle", post.getTitle());
            }
        }

        return result;
    }

    @Override
    public boolean deleteComment(Long id) {
        return commentMapper.deleteById(id) > 0;
    }

    @Override
    public boolean batchDeleteComments(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return false;
        }
        for (Long id : ids) {
            commentMapper.deleteById(id);
        }
        return true;
    }

    @Override
    public boolean updateCommentStatus(Long id, Short status) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(status);
        return commentMapper.updateById(comment) > 0;
    }

    @Override
    public boolean updateCommentContent(Long id, String content) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setContent(content);
        return commentMapper.updateById(comment) > 0;
    }

    @Override
    public Map<String, Object> getCommentStats() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        Long totalComments = commentMapper.selectCount(queryWrapper);
        stats.put("totalComments", totalComments);

        // 按状态统计
        QueryWrapper<Comment> activeWrapper = new QueryWrapper<>();
        activeWrapper.eq("status", (short) 1);
        Long activeComments = commentMapper.selectCount(activeWrapper);
        stats.put("activeComments", activeComments);

        QueryWrapper<Comment> hiddenWrapper = new QueryWrapper<>();
        hiddenWrapper.eq("status", (short) 0);
        Long hiddenComments = commentMapper.selectCount(hiddenWrapper);
        stats.put("hiddenComments", hiddenComments);

        return stats;
    }
}
