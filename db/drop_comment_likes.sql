-- 删除 comment 表中的 likes 字段（使用 post_like 表存储点赞关系）
-- 执行前请备份数据库

ALTER TABLE `comment` DROP COLUMN `likes`;
