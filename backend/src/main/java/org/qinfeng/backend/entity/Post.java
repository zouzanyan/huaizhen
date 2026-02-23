package org.qinfeng.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author qinfeng
 * @since 2025-12-07
 */
@Getter
@Setter
@ToString
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long forumId;

    private Long userId;

    private String title;

    private String content;

    private Short status;

    /**
     * 内容类型：0-普通文本，1-Markdown
     */
    private Integer contentType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
