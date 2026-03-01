-- 添加评论管理菜单
-- 注意：执行前请根据实际情况调整 ID

-- 1. 添加菜单项（假设 ID 为 24，如果冲突请修改）
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`)
VALUES (24, '评论管理', 0, 6, 'comment:view', 1, NOW(), NOW(), '/forum/comment', 'ChatDotRound');

-- 2. 为管理员角色分配此菜单权限（假设管理员角色 ID 为 1）
-- 如果您的管理员角色 ID 不是 1，请修改下面的值
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (1, 24);

-- 执行完成后，刷新页面或重新登录即可在侧边栏看到 "评论管理" 菜单项
