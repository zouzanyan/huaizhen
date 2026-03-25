package org.qinfeng.backend.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Mock评论数据传输对象
 *
 * @author qinfeng
 * @since 2026-03-01
 */
@Data
public class MockCommentDTO {

    /**
     * 帖子ID（必填）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long postId;

    /**
     * 用户ID（必填）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 父评论ID（可选，不传则为顶层评论，传值则为回复）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /**
     * 评论内容（必填）
     */
    private String content;

    /**
     * 状态（0-隐藏，1-显示，默认：1）
     */
    private Short status;
}
