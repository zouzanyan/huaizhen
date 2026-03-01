package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.qinfeng.backend.dto.MockCommentDTO;
import org.qinfeng.backend.dto.MockPostDTO;
import org.qinfeng.backend.dto.MockUserDTO;
import org.qinfeng.backend.entity.*;
import org.qinfeng.backend.mapper.*;
import org.qinfeng.backend.service.MockDataService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Mock数据生成服务实现
 *
 * @author qinfeng
 * @since 2026-03-01
 */
@Service
@RequiredArgsConstructor
public class MockDataServiceImpl implements MockDataService {

    private final UsersMapper userMapper;
    private final ForumMapper forumMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final PostLikeMapper postLikeMapper;
    private final PostFavoriteMapper postFavoriteMapper;
    private final AttachmentMapper attachmentMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Random random = new Random();

    // 用户名和昵称池（用于自动生成）
    private static final String[] USERNAMES = {
            "programmer", "developer", "coder", "geek", "techlead", "architect",
            "frontend", "backend", "fullstack", "devops", "designer", "pm"
    };

    private static final String[] NICKNAMES = {
            "码农小王", "前端达人", "后端大神", "全栈工程师", "架构师老张", "技术小白",
            "代码诗人", "Bug终结者", "性能优化专家", "安全专家", "云原生爱好者", "开源贡献者",
            "技术博主", "独立开发者", "创业程序员", "资深架构师", "数据库专家", "算法工程师",
            "移动开发专家", "测试工程师", "运维专家", "产品经理", "UI设计师", "技术总监"
    };

    @Override
    public Long createUser(MockUserDTO userDTO) {
        User user = new User();

        // 用户名：传入则使用，否则自动生成
        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            user.setUsername(userDTO.getUsername());
        } else {
            user.setUsername(USERNAMES[random.nextInt(USERNAMES.length)] + System.currentTimeMillis());
        }

        // 昵称：传入则使用，否则随机选择
        if (userDTO.getNickname() != null && !userDTO.getNickname().isEmpty()) {
            user.setNickname(userDTO.getNickname());
        } else {
            user.setNickname(NICKNAMES[random.nextInt(NICKNAMES.length)]);
        }

        // 密码：传入则使用，否则使用默认密码
        String password = (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty())
                ? userDTO.getPassword()
                : "mockmockmockhahahaha";
        user.setPasswordHash(passwordEncoder.encode(password));

        // 邮箱：传入则使用，否则根据用户名生成
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            user.setEmail(userDTO.getEmail());
        } else {
            user.setEmail(user.getUsername() + "@example.com");
        }

        // 头像：传入则使用，否则随机生成
        if (userDTO.getAvatarUrl() != null && !userDTO.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(userDTO.getAvatarUrl());
        } else {
            user.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + user.getUsername());
        }

        // 角色：默认为普通用户
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : (short) 0);

        // 状态：默认为激活
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : (short) 1);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public Long createPost(MockPostDTO postDTO) {
        // 验证必填字段
        if (postDTO.getForumId() == null || postDTO.getUserId() == null) {
            throw new IllegalArgumentException("forumId 和 userId 不能为空");
        }
        if (postDTO.getTitle() == null || postDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (postDTO.getContent() == null || postDTO.getContent().isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }

        // 验证用户和版块是否存在
        User user = userMapper.selectById(postDTO.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + postDTO.getUserId());
        }
        Forum forum = forumMapper.selectById(postDTO.getForumId());
        if (forum == null) {
            throw new IllegalArgumentException("版块不存在: " + postDTO.getForumId());
        }

        Post post = new Post();
        post.setForumId(postDTO.getForumId());
        post.setUserId(postDTO.getUserId());
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setContentType(postDTO.getContentType() != null ? postDTO.getContentType() : 1);
        post.setStatus(postDTO.getStatus() != null ? postDTO.getStatus() : (short) 1);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postMapper.insert(post);
        return post.getId();
    }

    @Override
    public Long createComment(MockCommentDTO commentDTO) {
        // 验证必填字段
        if (commentDTO.getPostId() == null || commentDTO.getUserId() == null) {
            throw new IllegalArgumentException("postId 和 userId 不能为空");
        }
        if (commentDTO.getContent() == null || commentDTO.getContent().isEmpty()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }

        // 验证用户和帖子是否存在
        User user = userMapper.selectById(commentDTO.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + commentDTO.getUserId());
        }
        Post post = postMapper.selectById(commentDTO.getPostId());
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在: " + commentDTO.getPostId());
        }

        // 如果是回复，验证父评论是否存在
        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentMapper.selectById(commentDTO.getParentId());
            if (parentComment == null) {
                throw new IllegalArgumentException("父评论不存在: " + commentDTO.getParentId());
            }
            // 验证父评论是否属于同一个帖子
            if (!parentComment.getPostId().equals(commentDTO.getPostId())) {
                throw new IllegalArgumentException("父评论不属于该帖子");
            }
        }

        Comment comment = new Comment();
        comment.setPostId(commentDTO.getPostId());
        comment.setUserId(commentDTO.getUserId());
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setStatus(commentDTO.getStatus() != null ? commentDTO.getStatus() : (short) 1);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentMapper.insert(comment);
        return comment.getId();
    }

    @Override
    public void createLike(Long postId, Long userId) {
        // 验证是否存在
        if (postMapper.selectById(postId) == null) {
            throw new IllegalArgumentException("帖子不存在: " + postId);
        }
        if (userMapper.selectById(userId) == null) {
            throw new IllegalArgumentException("用户不存在: " + userId);
        }

        // 检查是否已经点赞
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).eq("user_id", userId);
        if (postLikeMapper.selectCount(queryWrapper) > 0) {
            throw new IllegalArgumentException("已经点赞过了");
        }

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLike.setCreatedAt(LocalDateTime.now());

        postLikeMapper.insert(postLike);
    }

    @Override
    public void createFavorite(Long postId, Long userId) {
        // 验证是否存在
        if (postMapper.selectById(postId) == null) {
            throw new IllegalArgumentException("帖子不存在: " + postId);
        }
        if (userMapper.selectById(userId) == null) {
            throw new IllegalArgumentException("用户不存在: " + userId);
        }

        // 检查是否已经收藏
        QueryWrapper<PostFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).eq("user_id", userId);
        if (postFavoriteMapper.selectCount(queryWrapper) > 0) {
            throw new IllegalArgumentException("已经收藏过了");
        }

        PostFavorite postFavorite = new PostFavorite();
        postFavorite.setPostId(postId);
        postFavorite.setUserId(userId);
        postFavorite.setCreatedAt(LocalDateTime.now());

        postFavoriteMapper.insert(postFavorite);
    }

    @Override
    public Object getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalForums", forumMapper.selectCount(null));
        stats.put("totalPosts", postMapper.selectCount(null));
        stats.put("totalComments", commentMapper.selectCount(null));
        stats.put("totalLikes", postLikeMapper.selectCount(null));
        stats.put("totalFavorites", postFavoriteMapper.selectCount(null));

        return stats;
    }
}
