-- ----------------------------
-- Table structure for sys_prompt_category
-- ----------------------------
DROP TABLE IF EXISTS `prompt_category`;
CREATE TABLE `prompt_category` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(128) NOT NULL COMMENT '分类名称',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提示词分类表';

-- ----------------------------
-- Records of sys_prompt_category
-- ----------------------------
BEGIN;
INSERT INTO `prompt_category` (`id`, `name`, `sort`) VALUES (1, '通用助手', 1);
INSERT INTO `prompt_category` (`id`, `name`, `sort`) VALUES (2, '代码开发', 2);
INSERT INTO `prompt_category` (`id`, `name`, `sort`) VALUES (3, '文案写作', 3);
COMMIT;

-- ----------------------------
-- Table structure for sys_prompt
-- ----------------------------
DROP TABLE IF EXISTS `prompt`;
CREATE TABLE `prompt` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `title` varchar(255) NOT NULL COMMENT '标题',
    `content` text NOT NULL COMMENT '提示词内容',
    `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提示词表';

-- ----------------------------
-- Records of sys_prompt
-- ----------------------------
BEGIN;
INSERT INTO `prompt` (`category_id`, `title`, `content`, `status`) VALUES
(1, '智能助手', '你是一个智能助手，请根据用户的问题给出准确、有帮助的回答。', 1),
(2, '代码审查助手', '你是一个专业的代码审查专家。请审查用户提供的代码，指出潜在的问题、安全漏洞、性能问题，并给出改进建议。', 1),
(2, 'SQL优化专家', '你是一个SQL优化专家。请分析用户提供的SQL语句，指出性能问题并给出优化建议。', 1),
(3, '文章润色助手', '你是一个专业的文字编辑。请帮助用户润色文章，使其更加流畅、专业、有吸引力。', 1);
COMMIT;

-- ----------------------------
-- Add menu for prompt management
-- ----------------------------
INSERT INTO `sys_menu` (`name`, `parent_id`, `order_no`, `permission_code`, `status`, `path`, `icon`) 
VALUES ('提示词管理', 2, 4, 'prompt:view', 1, '/prompt', 'ChatDotRound');

-- 获取刚插入的菜单ID并分配给管理员角色
SET @prompt_menu_id = LAST_INSERT_ID();
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @prompt_menu_id);
