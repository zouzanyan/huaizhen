-- ----------------------------
-- Table structure for ai_api
-- ----------------------------
DROP TABLE IF EXISTS `ai_api`;
CREATE TABLE `ai_api` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(128) NOT NULL COMMENT '配置名称',
    `provider` varchar(64) NOT NULL COMMENT '提供商：openai, anthropic, google, custom等',
    `base_url` varchar(512) NOT NULL COMMENT 'API基础URL',
    `api_key` varchar(512) NOT NULL COMMENT 'API密钥',
    `model_name` varchar(128) NOT NULL COMMENT '模型名称',
    `max_tokens` int DEFAULT 4096 COMMENT '最大token数',
    `temperature` decimal(3,2) DEFAULT 0.70 COMMENT '温度参数',
    `status` int NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI API配置表';

-- ----------------------------
-- Records of ai_api
-- ----------------------------
BEGIN;
INSERT INTO `ai_api` (`name`, `provider`, `base_url`, `api_key`, `model_name`, `max_tokens`, `temperature`, `status`, `remark`) VALUES 
('GPT-4o', 'openai', 'https://api.openai.com/v1', 'sk-xxx', 'gpt-4o', 4096, 0.70, 1, 'OpenAI GPT-4o模型'),
('Claude 3.5 Sonnet', 'anthropic', 'https://api.anthropic.com/v1', 'sk-ant-xxx', 'claude-3-5-sonnet-20241022', 4096, 0.70, 1, 'Anthropic Claude 3.5 Sonnet模型'),
('Gemini Pro', 'google', 'https://generativelanguage.googleapis.com/v1', 'xxx', 'gemini-pro', 4096, 0.70, 0, 'Google Gemini Pro模型');
COMMIT;

-- ----------------------------
-- Add menu for AI API management
-- ----------------------------
INSERT INTO `sys_menu` (`name`, `parent_id`, `order_no`, `permission_code`, `status`, `path`, `icon`) 
VALUES ('AI配置', 0, 5, 'ai-api:view', 1, '', 'Cpu');

SET @ai_menu_id = LAST_INSERT_ID();
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @ai_menu_id);

INSERT INTO `sys_menu` (`name`, `parent_id`, `order_no`, `permission_code`, `status`, `path`, `icon`) 
VALUES ('API管理', @ai_menu_id, 1, 'ai-api:view', 1, '/ai/ai-api', 'Connection');

SET @ai_api_menu_id = LAST_INSERT_ID();
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @ai_api_menu_id);
