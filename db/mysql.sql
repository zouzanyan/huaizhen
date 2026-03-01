/*
 Navicat Premium Data Transfer

 Source Server         : 
 Source Server Type    : MySQL
 Source Server Version : 80100 (8.1.0)
 Source Host           : 
 Source Schema         : vk

 Target Server Type    : MySQL
 Target Server Version : 80100 (8.1.0)
 File Encoding         : 65001

 Date: 02/03/2026 00:20:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attachment
-- ----------------------------
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment`  (
  `id` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `size` int NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_attachment_post_id`(`post_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attachment
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `parent_id` bigint NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_comment_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_comment_post_id_created_at`(`post_id` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (2025562952527343618, 2025211907208998913, 2019296824288088066, NULL, '虚拟线程将是未来', 1, '2026-02-22 21:26:56', '2026-02-22 21:26:56');
INSERT INTO `comment` VALUES (2025584025829486593, 2025211907208998913, 2019296824288088066, NULL, '看好', 1, '2026-02-22 22:50:40', '2026-02-22 22:50:40');
INSERT INTO `comment` VALUES (2028105908413476866, 2025211907208998913, 2028103572148400130, NULL, '学习了', 1, '2026-03-01 21:51:44', '2026-03-01 21:51:44');
INSERT INTO `comment` VALUES (2028107521509240834, 2025211907208998913, 2028103572148400130, 2028105908413476866, '同意楼上的观点', 1, '2026-03-01 21:58:08', '2026-03-01 21:58:08');
INSERT INTO `comment` VALUES (2028118781655199745, 2028118761543512066, 2028118703766974466, NULL, '很棒的分享！学到了很多', 1, '2026-03-01 22:42:53', '2026-03-01 22:42:53');
INSERT INTO `comment` VALUES (2028118782238208001, 2028118761543512066, 2028118703766974466, NULL, '请问博主有代码示例吗？', 1, '2026-03-01 22:42:53', '2026-03-01 22:42:53');
INSERT INTO `comment` VALUES (2028118782829604865, 2028118761543512066, 2028118703766974466, NULL, '正好遇到这个问题，太及时了！', 1, '2026-03-01 22:42:53', '2026-03-01 22:42:53');
INSERT INTO `comment` VALUES (2028118783421001729, 2028118762067800065, 2028118703766974466, NULL, '很棒的分享！学到了很多', 1, '2026-03-01 22:42:53', '2026-03-01 22:42:53');
INSERT INTO `comment` VALUES (2028118783945289729, 2028118762067800065, 2028118703766974466, NULL, '请问博主有代码示例吗？', 1, '2026-03-01 22:42:53', '2026-03-01 22:42:53');
INSERT INTO `comment` VALUES (2028118784532492290, 2028118762067800065, 2028118703766974466, NULL, '正好遇到这个问题，太及时了！', 1, '2026-03-01 22:42:54', '2026-03-01 22:42:54');
INSERT INTO `comment` VALUES (2028134407996116993, 2028118761543512066, 2019296824288088066, NULL, '是的', 1, '2026-03-01 23:44:58', '2026-03-01 23:44:58');

-- ----------------------------
-- Table structure for forum
-- ----------------------------
DROP TABLE IF EXISTS `forum`;
CREATE TABLE `forum`  (
  `id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `parent_id` bigint NULL DEFAULT NULL,
  `status` int NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_forum_name`(`name` ASC) USING BTREE,
  INDEX `idx_forum_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forum
-- ----------------------------
INSERT INTO `forum` VALUES (1, '技术', '技术交流', NULL, 1, '2026-02-04 03:38:40', '2026-02-23 12:22:06');
INSERT INTO `forum` VALUES (2025907742041223170, 'AI', '大模型相关', NULL, 1, '2026-02-23 12:17:00', '2026-02-23 12:19:19');

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint NOT NULL,
  `forum_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int NOT NULL DEFAULT 0,
  `views` int NOT NULL DEFAULT 0,
  `likes` int NOT NULL DEFAULT 0,
  `content_type` int NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_forum_id_created_at`(`forum_id` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_post_user_id_created_at`(`user_id` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (2025211907208998913, 1, 2019296824288088066, 'JDK 25 时代的虚拟线程：从特性到基础设施能力', '## 一、虚拟线程的现状\r\n\r\n虚拟线程自 JDK 21 正式发布以来，经过多个版本演进，在 JDK 25 中已具备：\r\n\r\n- 更成熟的调度优化\r\n- 更稳定的 pinned 线程处理机制\r\n- 更完善的监控支持（JFR、JMX 指标）\r\n- 与主流框架的默认兼容\r\n\r\n它不再是“是否尝试”的问题，而是“在什么场景下默认使用”。\r\n\r\n------\r\n\r\n## 二、并发模型的现实问题\r\n\r\n在典型的 Java Web 服务中：\r\n\r\n- 每个请求需要访问数据库\r\n- 调用多个下游 RPC\r\n- 等待缓存或外部 HTTP 接口\r\n\r\nCPU 实际计算时间很短，大量时间耗在 IO 等待。\r\n\r\n传统线程模型存在两个长期矛盾：\r\n\r\n1. 线程数量不能无限增加\r\n2. 阻塞会浪费操作系统线程资源\r\n\r\n线程池调优，本质是在控制风险，而不是提高资源利用率。\r\n\r\n------\r\n\r\n## 三、JDK 25 下虚拟线程的核心机制\r\n\r\n虚拟线程仍然基于：\r\n\r\n- Continuation\r\n- JVM 调度器\r\n- 少量载体线程（Carrier Threads）\r\n\r\n当虚拟线程发生阻塞：\r\n\r\n- JVM 保存执行状态\r\n- 从载体线程卸载\r\n- 载体线程立即执行其他任务\r\n\r\n因此：\r\n\r\n- 阻塞不再等于占用 OS 线程\r\n- 并发能力由内存决定，而不是线程数\r\n\r\n这一点在 IO 密集型服务中影响非常直接。\r\n\r\n------\r\n\r\n## 四、生产环境中的实践效果\r\n\r\n在实际压测中常见表现：\r\n\r\n- 平台线程数量维持在较低水平\r\n- 虚拟线程数量可达数万\r\n- CPU 利用率更稳定\r\n- 吞吐量提升明显\r\n- 响应延迟更平滑\r\n\r\n特别是在数据库或 RPC 调用频繁的聚合接口场景，提升更明显。\r\n\r\n------\r\n\r\n## 五、与响应式模型的对比\r\n\r\n响应式框架仍然有其优势，尤其在：\r\n\r\n- 极端高并发场景\r\n- 高度事件驱动系统\r\n\r\n但虚拟线程的优势在于：\r\n\r\n- 保持同步编程风格\r\n- 几乎不改业务代码\r\n- 更低的迁移成本\r\n- 更好的可读性\r\n\r\n在 JDK 25 环境下，大多数 IO 密集型服务已经不需要为了性能强制改写为响应式。\r\n\r\n------\r\n\r\n## 六、仍需关注的问题\r\n\r\n### 1. Pinned 线程\r\n\r\n长时间持有 synchronized 锁，仍可能导致虚拟线程无法卸载。\r\n\r\n建议：\r\n\r\n- 使用 ReentrantLock\r\n- 减少长时间临界区\r\n\r\n### 2. ThreadLocal 使用\r\n\r\n大量虚拟线程意味着 ThreadLocal 数量剧增，需避免滥用。\r\n\r\n### 3. CPU 密集任务\r\n\r\n虚拟线程不会提升单核算力。对于纯计算任务，应关注并行流或 ForkJoinPool。\r\n\r\n------\r\n\r\n## 七、当前建议\r\n\r\n在 JDK 25 环境下：\r\n\r\n- 新项目默认启用虚拟线程\r\n- IO 密集型微服务优先采用\r\n- 保持同步编程模型\r\n- 避免过早引入复杂响应式体系\r\n\r\n线程池调优正在逐步退出日常优化重点。虚拟线程更像是一种运行时能力升级，而不是框架层面的重构。\r\n\r\n在当下版本周期里，它已经成为 Java 并发体系的主流路径，而不是可选项。', 1, 0, 0, 1, '2026-02-21 22:12:00', '2026-02-21 22:12:00');
INSERT INTO `post` VALUES (2027337945489211394, 2025907742041223170, 2019296824288088066, 'openclaw部署', 'openclaw部署相关', 1, 0, 0, 0, '2026-02-27 19:00:07', '2026-02-27 19:00:07');
INSERT INTO `post` VALUES (2028105089463037953, 1, 2028103572148400130, 'Spring Boot技巧', '分享一些实用技巧...', 1, 0, 0, 1, '2026-03-01 21:48:28', '2026-03-01 21:48:28');
INSERT INTO `post` VALUES (2028118761543512066, 1, 2028118703766974466, '如何优化数据库查询性能？', '大家好，今天想和大家分享一些开发经验。\n\n## 核心要点\n\n1. 理解业务需求\n2. 选择合适的技术栈\n3. 注重代码质量\n\n希望能帮助到有需要的朋友！', 0, 0, 0, 1, '2026-03-01 22:42:48', '2026-03-01 15:02:42');
INSERT INTO `post` VALUES (2028118762067800065, 1, 2028118703766974466, 'Spring Boot 最佳实践分享', '最近在项目中使用了一些新技术，效果非常不错。\n\n## 使用场景\n\n...\n\n## 实现步骤\n\n...\n\n欢迎大家一起讨论！', 1, 0, 0, 1, '2026-03-01 22:42:48', '2026-03-01 22:42:48');

-- ----------------------------
-- Table structure for post_favorite
-- ----------------------------
DROP TABLE IF EXISTS `post_favorite`;
CREATE TABLE `post_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_post_favorite`(`user_id` ASC, `post_id` ASC) USING BTREE,
  INDEX `idx_post_favorite_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_post_favorite_user_id_created_at`(`user_id` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2025585350927560707 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_favorite
-- ----------------------------
INSERT INTO `post_favorite` VALUES (2025585350927560706, 2025211907208998913, 2019296824288088066, '2026-02-22 22:55:56');

-- ----------------------------
-- Table structure for post_like
-- ----------------------------
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_post_like_user_post`(`user_id` ASC, `post_id` ASC) USING BTREE,
  INDEX `idx_post_like_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_post_like_user_id_created_at`(`user_id` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2028118814119112707 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_like
-- ----------------------------
INSERT INTO `post_like` VALUES (2025585333139517442, 2025211907208998913, 2019296824288088066, '2026-02-22 22:55:52');
INSERT INTO `post_like` VALUES (2028118814119112706, 2028118761543512066, 2028118703766974466, '2026-03-01 22:43:01');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` bigint NULL DEFAULT 0,
  `order_no` int NULL DEFAULT 0,
  `permission_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` int NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '仪表盘', 0, 1, 'dashboard:view', 1, '2026-02-09 15:37:04', '2026-02-12 07:15:41', '/dashboard', 'Odometer');
INSERT INTO `sys_menu` VALUES (2, '系统管理', 0, 10, '', 1, '2026-02-09 15:37:04', '2026-02-23 04:03:46', '', 'Setting');
INSERT INTO `sys_menu` VALUES (3, '用户管理', 2, 1, 'user:view', 1, '2026-02-09 15:37:04', '2026-02-09 15:41:39', '/system/user', 'User');
INSERT INTO `sys_menu` VALUES (4, '角色管理', 2, 2, 'role:view', 1, '2026-02-09 15:37:04', '2026-02-09 15:41:39', '/system/role', 'Avatar');
INSERT INTO `sys_menu` VALUES (8, '菜单管理', 2, 3, 'menu:view', 1, '2026-02-10 07:16:07', '2026-02-10 07:18:48', '/system/menu', 'Menu');
INSERT INTO `sys_menu` VALUES (20, '用户管理', 0, 2, '', 1, '2026-02-23 04:03:10', '2026-02-23 10:21:36', '/forum/user', 'Avatar');
INSERT INTO `sys_menu` VALUES (21, '节点管理', 0, 3, '', 1, '2026-02-23 04:04:33', '2026-02-23 13:49:38', '/forum/board', 'CirclePlus');
INSERT INTO `sys_menu` VALUES (22, '帖文管理', 0, 4, '', 1, '2026-02-23 04:05:02', '2026-02-23 10:21:51', '/forum/post', 'DocumentCopy');
INSERT INTO `sys_menu` VALUES (23, 'Mock数据', 0, 5, 'mock:view', 1, '2026-03-01 14:40:36', '2026-03-01 14:40:36', '/mock/data', 'MagicStick');
INSERT INTO `sys_menu` VALUES (24, '评论管理', 0, 6, 'comment:view', 1, '2026-03-01 15:12:39', '2026-03-01 15:12:39', '/forum/comment', 'ChatDotRound');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_permission_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, 'user:view', '查看用户', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` VALUES (2, 'user:create', '创建用户', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` VALUES (3, 'user:update', '更新用户', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` VALUES (4, 'user:delete', '删除用户', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` VALUES (5, 'role:view', '查看角色', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` VALUES (6, 'role:create', '创建角色', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` VALUES (7, 'role:update', '更新角色', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` VALUES (8, 'role:delete', '删除角色', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` VALUES (9, 'log:view', '查看日志', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_role_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'ADMIN', '管理员', 1, '2026-02-09 08:49:30', '2026-02-09 08:49:30');
INSERT INTO `sys_role` VALUES (2, 'USER', '普通用户', 1, '2026-02-09 08:49:30', '2026-02-09 08:49:30');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 135 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (125, 1, 1);
INSERT INTO `sys_role_menu` VALUES (131, 1, 2);
INSERT INTO `sys_role_menu` VALUES (132, 1, 3);
INSERT INTO `sys_role_menu` VALUES (133, 1, 4);
INSERT INTO `sys_role_menu` VALUES (134, 1, 8);
INSERT INTO `sys_role_menu` VALUES (126, 1, 20);
INSERT INTO `sys_role_menu` VALUES (127, 1, 21);
INSERT INTO `sys_role_menu` VALUES (128, 1, 22);
INSERT INTO `sys_role_menu` VALUES (129, 1, 23);
INSERT INTO `sys_role_menu` VALUES (130, 1, 24);
INSERT INTO `sys_role_menu` VALUES (112, 2, 1);
INSERT INTO `sys_role_menu` VALUES (113, 2, 20);
INSERT INTO `sys_role_menu` VALUES (114, 2, 21);
INSERT INTO `sys_role_menu` VALUES (115, 2, 22);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (9, 1, 1);
INSERT INTO `sys_role_permission` VALUES (6, 1, 2);
INSERT INTO `sys_role_permission` VALUES (8, 1, 3);
INSERT INTO `sys_role_permission` VALUES (7, 1, 4);
INSERT INTO `sys_role_permission` VALUES (5, 1, 5);
INSERT INTO `sys_role_permission` VALUES (2, 1, 6);
INSERT INTO `sys_role_permission` VALUES (4, 1, 7);
INSERT INTO `sys_role_permission` VALUES (3, 1, 8);
INSERT INTO `sys_role_permission` VALUES (1, 1, 9);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$63y5d4HsKCpgaQ/YFxhrnuOoYy8lmx7hjV.mVIDHNE2qsmyd1Qi7e', 1, '2026-02-09 08:49:30', '2026-02-09 08:51:58');
INSERT INTO `sys_user` VALUES (2, 'qinfeng', '$2a$10$9MO.89HEchl1oKb/GfzQEeRmPLE7lKFD2YHasEEQL3QwYBbj8ylbe', 1, '2026-02-09 15:16:57', '2026-02-23 09:22:18');
INSERT INTO `sys_user` VALUES (3, 'xia', '$2a$10$Bmkh6AS0bGNu98DNspPDZ.K74Oy4JPOuY1nNg9G5muSD4ZDJLR57i', 1, '2026-03-01 16:18:44', '2026-03-01 16:18:44');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sys_user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_role`(`sys_user_id` ASC, `role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (4, 1, 1);
INSERT INTO `sys_user_role` VALUES (6, 2, 2);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` int NOT NULL DEFAULT 0,
  `status` int NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_user_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uq_user_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2019296824288088066, 'root', '创始人', '$2a$10$CfldzfEWEep6eQR6vzl0T.Io1BOnEZhplMFdzTplndjzMmQA/flIe', '1@qq.com', 'https://ui-avatars.com/api/?name=root&background=random&color=fff', 1, 1, '2026-02-05 14:27:34', '2026-02-23 12:16:34');
INSERT INTO `user` VALUES (2028103572148400130, 'coder1772372546477', '技术达人', '$2a$10$7A/bstRGZVaEo.drPZpzAOCfG./FxzglL3AUp3.2LYjQ9.BWPmG5m', '772372546477@qq.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=coder1772372546477', 0, 1, '2026-03-01 21:42:27', '2026-03-01 13:43:43');
INSERT INTO `user` VALUES (2028118703766974466, 'geek1772376154190', '批量测试用户', '$2a$10$2wj6B36Qvs2Q1o/t73KuFu.dVJntMzGjhoo4cL9SBM.AXo.mDole2', 'geek1772376154190@example.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=geek1772376154190', 0, 1, '2026-03-01 22:42:34', '2026-03-01 22:42:34');
INSERT INTO `user` VALUES (2028119263635255297, 'programmer1772376287672', '测试工程师', '$2a$10$vpE1hflEd5Zsy8TBwd/e2ew.A0kxw9s0iH5CvoU9f3DUj8K9p8OYq', 'programmer1772376287672@example.com', 'https://api.dicebear.com/7.x/avataaars/svg?seed=programmer1772376287672', 0, 1, '2026-03-01 22:44:48', '2026-03-01 22:44:48');

SET FOREIGN_KEY_CHECKS = 1;
