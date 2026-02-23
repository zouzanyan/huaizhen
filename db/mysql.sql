/*
 Navicat Premium Dump SQL

 Source Server         :
 Source Server Type    : MySQL
 Source Server Version : 80100 (8.1.0)
 Source Host           :
 Source Schema         : huaizhen

 Target Server Type    : MySQL
 Target Server Version : 80100 (8.1.0)
 File Encoding         : 65001

 Date: 23/02/2026 21:02:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `name` varchar(128) NOT NULL,
    `parent_id` bigint DEFAULT '0',
    `order_no` int DEFAULT '0',
    `permission_code` varchar(128) DEFAULT NULL,
    `status` int NOT NULL DEFAULT '1',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `path` varchar(255) DEFAULT NULL COMMENT '路由路径',
    `icon` varchar(64) DEFAULT NULL COMMENT '图标名称',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`) VALUES (1, '仪表盘', 0, 1, 'dashboard:view', 1, '2026-02-09 15:37:04', '2026-02-12 07:15:41', '/dashboard', 'Odometer');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`) VALUES (2, '系统管理', 0, 10, '', 1, '2026-02-09 15:37:04', '2026-02-23 04:03:46', '', 'Setting');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`) VALUES (3, '用户管理', 2, 1, 'user:view', 1, '2026-02-09 15:37:04', '2026-02-09 15:41:39', '/system/user', 'User');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`) VALUES (4, '角色管理', 2, 2, 'role:view', 1, '2026-02-09 15:37:04', '2026-02-09 15:41:39', '/system/role', 'Avatar');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`) VALUES (8, '菜单管理', 2, 3, 'menu:view', 1, '2026-02-10 07:16:07', '2026-02-10 07:18:48', '/system/menu', 'Menu');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`) VALUES (20, '用户管理', 0, 2, '', 1, '2026-02-23 04:03:10', '2026-02-23 04:03:24', '', 'Avatar');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`) VALUES (21, '节点管理', 0, 3, '', 1, '2026-02-23 04:04:33', '2026-02-23 04:16:00', '', 'HelpFilled');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`) VALUES (22, '帖文管理', 0, 4, '', 1, '2026-02-23 04:05:02', '2026-02-23 04:05:02', '', 'DocumentCopy');
COMMIT;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `code` varchar(128) NOT NULL,
    `name` varchar(128) NOT NULL,
    `type` varchar(32) NOT NULL,
    `status` int NOT NULL DEFAULT '1',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_permission_code` (`code`)
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
BEGIN;
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `status`, `created_at`, `updated_at`) VALUES (1, 'user:view', '查看用户', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `status`, `created_at`, `updated_at`) VALUES (2, 'user:create', '创建用户', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `status`, `created_at`, `updated_at`) VALUES (3, 'user:update', '更新用户', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `status`, `created_at`, `updated_at`) VALUES (4, 'user:delete', '删除用户', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `status`, `created_at`, `updated_at`) VALUES (5, 'role:view', '查看角色', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `status`, `created_at`, `updated_at`) VALUES (6, 'role:create', '创建角色', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `status`, `created_at`, `updated_at`) VALUES (7, 'role:update', '更新角色', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `status`, `created_at`, `updated_at`) VALUES (8, 'role:delete', '删除角色', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `type`, `status`, `created_at`, `updated_at`) VALUES (9, 'log:view', '查看日志', 'api', 1, '2026-02-09 08:49:45', '2026-02-09 08:49:45');
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `code` varchar(64) NOT NULL,
    `name` varchar(64) NOT NULL,
    `status` int NOT NULL DEFAULT '1',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_code` (`code`)
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `code`, `name`, `status`, `created_at`, `updated_at`) VALUES (1, 'ADMIN', '管理员', 1, '2026-02-09 08:49:30', '2026-02-09 08:49:30');
INSERT INTO `sys_role` (`id`, `code`, `name`, `status`, `created_at`, `updated_at`) VALUES (2, 'USER', '普通用户', 1, '2026-02-09 08:49:30', '2026-02-09 08:49:30');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `role_id` bigint NOT NULL,
                                 `menu_id` bigint NOT NULL,
                                 PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_menu` (`role_id`,`menu_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (61, 1, 1);
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (62, 1, 2);
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (63, 1, 3);
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (64, 1, 4);
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (68, 1, 8);
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (98, 1, 20);
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (99, 1, 21);
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (100, 1, 22);
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (96, 2, 1);
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (97, 2, 2);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `role_id` bigint NOT NULL,
                                       `permission_id` bigint NOT NULL,
                                       PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_permission` (`role_id`,`permission_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (9, 1, 1);
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (6, 1, 2);
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (8, 1, 3);
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (7, 1, 4);
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (5, 1, 5);
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (2, 1, 6);
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (4, 1, 7);
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (3, 1, 8);
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (1, 1, 9);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `username` varchar(64) NOT NULL,
    `password_hash` varchar(255) NOT NULL,
    `status` int NOT NULL DEFAULT '1',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_username` (`username`)
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `username`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES (1, 'admin', '$2a$10$63y5d4HsKCpgaQ/YFxhrnuOoYy8lmx7hjV.mVIDHNE2qsmyd1Qi7e', 1, '2026-02-09 08:49:30', '2026-02-09 08:51:58');
INSERT INTO `sys_user` (`id`, `username`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES (2, 'qinfeng', '$2a$10$9MO.89HEchl1oKb/GfzQEeRmPLE7lKFD2YHasEEQL3QwYBbj8ylbe', 0, '2026-02-09 15:16:57', '2026-02-23 09:22:18');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `sys_user_id` bigint NOT NULL,
                                 `role_id` bigint NOT NULL,
                                 PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_role` (`sys_user_id`,`role_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`id`, `sys_user_id`, `role_id`) VALUES (4, 1, 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
