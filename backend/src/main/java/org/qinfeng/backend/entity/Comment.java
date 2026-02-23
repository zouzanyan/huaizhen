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
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long postId;

    private Long userId;

    private Long parentId;

    private String content;

    private Short status;

    private Integer likes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
