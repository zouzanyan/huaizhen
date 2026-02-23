package org.qinfeng.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.qinfeng.backend.entity.Post;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qinfeng
 * @since 2025-12-07
 */
public interface PostMapper extends BaseMapper<Post> {

    @Select("""
        SELECT
            p.id            AS id,
            p.title         AS title,
            p.content       AS content,
            p.user_id       AS userId,
            p.forum_id      AS forumId,
            p.content_type   AS contentType,
            p.status        AS status,
            p.created_at    AS createdAt,
            p.updated_at    AS updatedAt,
            u.username      AS authorName,
            u.avatar_url    AS authorAvatar,
            f.name          AS forumName
        FROM post p
        LEFT JOIN user u ON p.user_id = u.id
        LEFT JOIN forum f ON p.forum_id = f.id
        WHERE p.id = #{id}
    """)
    Map<String, Object> getPostDetail(@Param("id") Long id);

    @Select("""
        <script>
        SELECT
            p.id            AS id,
            p.title         AS title,
            p.content       AS content,
            p.user_id       AS userId,
            p.forum_id      AS forumId,
            p.content_type   AS contentType,
            p.status        AS status,
            p.created_at    AS createdAt,
            p.updated_at    AS updatedAt,
            u.username      AS authorName,
            u.avatar_url    AS authorAvatar,
            f.name          AS forumName
        FROM post p
        LEFT JOIN user u ON p.user_id = u.id
        LEFT JOIN forum f ON p.forum_id = f.id
        <where>
            <if test="keyword != null and keyword != ''">
                AND (p.title LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="forumId != null">
                AND p.forum_id = #{forumId}
            </if>
            <if test="status != null">
                AND p.status = #{status}
            </if>
        </where>
        ORDER BY p.created_at DESC
        </script>
    """)
    List<Map<String, Object>> getPostListWithDetails(
        @Param("keyword") String keyword,
        @Param("forumId") Long forumId,
        @Param("status") Short status
    );
}

