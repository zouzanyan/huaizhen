-- 知识图谱项目表
CREATE TABLE IF NOT EXISTS `kg_project` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(128) NOT NULL COMMENT '项目名称',
    `description` text COMMENT '项目描述',
    `status` int NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
    `create_user_id` bigint COMMENT '创建用户ID',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识图谱项目表';

-- 实体类型表
CREATE TABLE IF NOT EXISTS `kg_entity_type` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `project_id` bigint NOT NULL COMMENT '项目ID',
    `name` varchar(64) NOT NULL COMMENT '实体类型名称',
    `color` varchar(32) DEFAULT '#409EFF' COMMENT '显示颜色',
    `icon` varchar(64) COMMENT '图标',
    `description` varchar(255) COMMENT '描述',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='实体类型表';

-- 关系类型表
CREATE TABLE IF NOT EXISTS `kg_relation_type` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `project_id` bigint NOT NULL COMMENT '项目ID',
    `name` varchar(64) NOT NULL COMMENT '关系类型名称',
    `source_entity_type_id` bigint COMMENT '源实体类型ID',
    `target_entity_type_id` bigint COMMENT '目标实体类型ID',
    `description` varchar(255) COMMENT '描述',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='关系类型表';

-- 语料表
CREATE TABLE IF NOT EXISTS `kg_corpus` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `project_id` bigint NOT NULL COMMENT '项目ID',
    `title` varchar(255) COMMENT '标题',
    `content` text COMMENT '内容',
    `source` varchar(255) COMMENT '来源',
    `status` int NOT NULL DEFAULT '1' COMMENT '状态：1-未处理，2-已处理',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='语料表';

-- 插入示例数据
INSERT INTO `kg_project` (`name`, `description`, `status`, `create_user_id`) VALUES 
('示例知识图谱项目', '这是一个示例知识图谱项目，用于演示系统功能', 1, 1);

INSERT INTO `kg_entity_type` (`project_id`, `name`, `color`, `description`) VALUES 
(1, '人物', '#409EFF', '人物实体'),
(1, '地点', '#67C23A', '地点实体'),
(1, '组织', '#E6A23C', '组织机构'),
(1, '事件', '#F56C6C', '事件实体');

INSERT INTO `kg_relation_type` (`project_id`, `name`, `source_entity_type_id`, `target_entity_type_id`, `description`) VALUES 
(1, '位于', 2, 2, '地点之间的位置关系'),
(1, '属于', 1, 3, '人物属于某个组织'),
(1, '参与', 1, 4, '人物参与某个事件'),
(1, '发生地', 4, 2, '事件发生的地点');

INSERT INTO `kg_corpus` (`project_id`, `title`, `content`, `source`, `status`) VALUES 
(1, '示例语料', '张三是阿里巴巴的工程师，他参与了双11购物节活动的筹备工作。阿里巴巴位于杭州市。', '示例', 1);
