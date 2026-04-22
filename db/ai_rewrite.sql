-- ----------------------------
-- Table structure for ai_rewrite
-- ----------------------------
DROP TABLE IF EXISTS `ai_rewrite`;
CREATE TABLE `ai_rewrite` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(255) DEFAULT NULL COMMENT '标题',
    `original_text` text NOT NULL COMMENT '原文',
    `result_text` text COMMENT '仿写结果',
    `prompt_id` bigint DEFAULT NULL COMMENT '使用的提示词ID',
    `api_id` bigint NOT NULL COMMENT '使用的API配置ID',
    `model_name` varchar(128) DEFAULT NULL COMMENT '使用的模型名称',
    `status` int NOT NULL DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-成功，3-失败',
    `error_msg` varchar(512) DEFAULT NULL COMMENT '错误信息',
    `tokens_used` int DEFAULT NULL COMMENT '消耗的token数',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_api_id` (`api_id`),
    KEY `idx_prompt_id` (`prompt_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI仿写记录表';

-- ----------------------------
-- Add menu for AI Rewrite management
-- ----------------------------
INSERT INTO `sys_menu` (`name`, `parent_id`, `order_no`, `permission_code`, `status`, `path`, `icon`) 
VALUES ('仿写管理', (SELECT id FROM (SELECT id FROM sys_menu WHERE name = 'AI配置') AS tmp), 2, 'ai-rewrite:view', 1, '/rewrite', 'Edit');

SET @rewrite_menu_id = LAST_INSERT_ID();
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @rewrite_menu_id);
