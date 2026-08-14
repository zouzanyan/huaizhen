CREATE DATABASE IF NOT EXISTS qinfeng_forum
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE qinfeng_forum;


-- 用户
CREATE TABLE user (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      username VARCHAR(32) NOT NULL,
                      password_hash VARCHAR(255) NOT NULL,
                      nickname VARCHAR(32) NOT NULL,
                      status TINYINT NOT NULL DEFAULT 1,
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                      PRIMARY KEY (id),
                      UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 板块
CREATE TABLE board (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(32) NOT NULL,
                       description VARCHAR(255),
                       status TINYINT NOT NULL DEFAULT 1,
                       sort INT NOT NULL DEFAULT 0,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       PRIMARY KEY (id),
                       UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 帖子
CREATE TABLE post (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      user_id BIGINT NOT NULL,
                      board_id BIGINT NOT NULL,

                      title VARCHAR(200) NOT NULL,
                      content TEXT NOT NULL,

                      view_count INT NOT NULL DEFAULT 0,
                      like_count INT NOT NULL DEFAULT 0,
                      comment_count INT NOT NULL DEFAULT 0,

                      status TINYINT NOT NULL DEFAULT 1,
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                      PRIMARY KEY (id),
                      KEY idx_board_created (board_id, created_at),
                      KEY idx_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 评论
CREATE TABLE comment (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         post_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         parent_id BIGINT DEFAULT NULL,

                         content TEXT NOT NULL,

                         like_count INT NOT NULL DEFAULT 0,
                         status TINYINT NOT NULL DEFAULT 1,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         PRIMARY KEY (id),
                         KEY idx_post_created (post_id, created_at),
                         KEY idx_parent_created (parent_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 帖子点赞
CREATE TABLE post_like (
                           post_id BIGINT NOT NULL,
                           user_id BIGINT NOT NULL,
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           PRIMARY KEY (post_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;