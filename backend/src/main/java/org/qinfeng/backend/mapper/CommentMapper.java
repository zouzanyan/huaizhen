package org.qinfeng.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.qinfeng.backend.entity.Comment;
import org.qinfeng.backend.vo.CommentVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qinfeng
 * @since 2025-12-07
 */
public interface CommentMapper extends BaseMapper<Comment> {



    @Select("""
        SELECT
            c.id            AS id,
            c.user_id       AS userId,
            c.content       AS content,
            c.created_at   AS createdAt,

            u.nickname      AS authorName,
            u.avatar_url        AS authorAvatar
        FROM comment c
        LEFT JOIN user u ON c.user_id = u.id
        WHERE c.post_id = #{postId}
        ORDER BY c.created_at
    """)
    List<CommentVO> listByPostId(@Param("postId") Long postId);

    @Select("""
        SELECT
            c.id            AS id,
            c.user_id       AS userId,
            c.content       AS content,
            c.post_id       AS postId,
            c.created_at    AS createdAt,

            u.nickname      AS authorName,
            u.avatar_url    AS authorAvatar,
            p.title         AS postTitle
        FROM comment c
        LEFT JOIN user u ON c.user_id = u.id
        LEFT JOIN post p ON c.post_id = p.id
        WHERE c.user_id = #{userId}
        ORDER BY c.created_at DESC
    """)
    List<CommentVO> listByUserId(@Param("userId") Long userId);

}
