package org.qinfeng.backend.entity;

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
public class Forum implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String description;

    private Long parentId;

    private Short status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
