-- 更新菜单，添加知识图谱相关菜单项

-- 删除旧菜单
DELETE FROM sys_menu WHERE id >= 20;

-- 添加知识图谱相关菜单（一级菜单）
INSERT INTO sys_menu (`id`, `name`, `parent_id`, `order_no`, `permission_code`, `status`, `created_at`, `updated_at`, `path`, `icon`) VALUES 
(20, '图谱项目管理', 0, 2, '', 1, NOW(), NOW(), '/kg/project', 'Share'),
(21, '图谱模型管理', 0, 3, '', 1, NOW(), NOW(), '/kg/model', 'Connection'),
(22, '知识图谱探索', 0, 4, '', 1, NOW(), NOW(), '/kg/explore', 'DataLine'),
(23, '语料管理', 0, 5, '', 1, NOW(), NOW(), '/kg/corpus', 'Document'),
(24, '数据导入', 0, 6, '', 1, NOW(), NOW(), '/kg/transform', 'Upload');

-- 为管理员角色添加新菜单权限
INSERT INTO sys_role_menu (`role_id`, `menu_id`) VALUES 
(1, 20),
(1, 21),
(1, 22),
(1, 23),
(1, 24);
