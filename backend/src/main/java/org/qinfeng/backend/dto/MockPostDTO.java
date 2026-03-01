package org.qinfeng.backend.dto;

import lombok.Data;

/**
 * Mock帖子数据传输对象
 *
 * @author qinfeng
 * @since 2026-03-01
 */
@Data
public class MockPostDTO {

    /**
     * 版块ID（必填）
     */
    private Long forumId;

    /**
     * 用户ID（必填）
     */
    private Long userId;

    /**
     * 标题（必填）
     */
    private String title;

    /**
     * 内容（必填）
     */
    private String content;

    /**
     * 内容类型（0-普通文本，1-Markdown，默认：1）
     */
    private Integer contentType;

    /**
     * 状态（0-草稿，1-已发布，默认：1）
     */
    private Short status;
}
