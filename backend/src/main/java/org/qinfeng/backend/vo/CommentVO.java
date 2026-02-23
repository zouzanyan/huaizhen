package org.qinfeng.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {

    private Long id;
    private Long userId;
    private String content;
    private Long postId;
    private String postTitle;
    private LocalDateTime createdAt;

    // author
    private String authorName;
    private String authorAvatar;
}
