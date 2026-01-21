/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80404 (8.4.4)
 Source Host           : localhost:3306
 Source Schema         : junoyi

 Target Server Type    : MySQL
 Target Server Version : 80404 (8.4.4)
 File Encoding         : 65001

 Date: 21/01/2026 21:19:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门主键ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父部门ID',
  `name` varchar(50) DEFAULT NULL COMMENT '部门名称',
  `sort` int DEFAULT NULL COMMENT '排序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phonenumber` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` int DEFAULT '1' COMMENT '状态（0禁用，1正常）',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标识（软删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统部门权限表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 0, 'JunoYi网络科技有限公司', 1, '小饭', '18877667671', 'admin@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:00:44', 'super_admin', '2026-01-04 14:50:55', '主公司');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 1, '杭州总公司', 1, '小饭', '18877667671', 'admin@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:03:41', 'super_admin', '2026-01-04 14:50:55', '总公司');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 1, '上海分公司', 2, '小王', '17788667643', 'user1@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:05:12', 'super_admin', '2026-01-04 14:50:55', '分公司');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 2, '研发部门', 2, '小苏', '19988333223', 'user2@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:06:30', 'super_admin', '2026-01-04 14:50:55', '研发部门');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 2, '市场部门', 3, '小刘', '19984346473', 'user3@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:07:31', 'super_admin', '2026-01-04 14:50:55', '市场部门');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 2, '财务部门', 1, '小程', '13888448323', 'user4@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:08:21', 'super_admin', '2026-01-04 14:50:55', '财务部门');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 0, '测试部门1', 0, '测试1222', '18888888888', 'test@junoyi.com', 0, 1, 'super_admin', '2026-01-02 14:35:35', 'super_admin', '2026-01-02 14:45:10', '');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 7, '测试部门2', 0, '测试2', '18888888888', 'test2@junoyi.com', 0, 1, 'super_admin', '2026-01-02 14:44:55', 'super_admin', '2026-01-02 14:45:07', '');
COMMIT;

-- ----------------------------
-- Table structure for sys_dept_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_group`;
CREATE TABLE `sys_dept_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `group_id` bigint DEFAULT NULL COMMENT '权限组ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（临时权限）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门-权限组关联表';

-- ----------------------------
-- Records of sys_dept_group
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID ( 0 表示顶级）',
  `name` varchar(100) DEFAULT NULL COMMENT '路由名称',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `title` varchar(100) DEFAULT NULL COMMENT '菜单标题（支持 i18n key)',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `menu_type` tinyint DEFAULT NULL COMMENT '菜单类型( 0目录 1菜单 2按钮)',
  `sort` int DEFAULT NULL COMMENT '排序号',
  `permission` varchar(200) DEFAULT NULL COMMENT '权限标识符',
  `is_hide` tinyint DEFAULT '0' COMMENT '是否隐藏菜单（0否 1是）',
  `is_hide_tab` tinyint DEFAULT '0' COMMENT '是否隐藏标签页（0否 1是）',
  `keep_alive` tinyint DEFAULT '1' COMMENT '是否缓存（0 否 1是）',
  `is_iframe` tinyint DEFAULT '0' COMMENT '是否iframe：0=否, 1=是',
  `link` varchar(500) DEFAULT NULL COMMENT '外部链接地址',
  `is_full_page` tinyint DEFAULT '0' COMMENT '是否全屏页面：0=否, 1=是',
  `fixed_tab` int DEFAULT '0' COMMENT '是否固定标签页0=否, 1=是',
  `active_path` varchar(200) DEFAULT NULL COMMENT '激活菜单路径（详情页用）',
  `show_badge` tinyint DEFAULT '0' COMMENT '是否显示徽章',
  `show_text_badge` varchar(50) DEFAULT NULL COMMENT '文本徽章内容（如 New）',
  `status` tinyint DEFAULT '1' COMMENT '状态：0=禁用, 1=启用',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 0, 'Dashboard', '/dashboard', '/index/index', 'menus.dashboard.title', 'ri:pie-chart-line', 0, 1, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '仪表盘目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 1, 'Console', 'console', '/dashboard/console', 'menus.dashboard.console', 'ri:home-smile-2-line', 1, 1, NULL, 0, 0, 0, 0, NULL, 0, 1, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '控制台');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 1, 'Analysis', 'analysis', '/dashboard/analysis', 'menus.dashboard.analysis', 'ri:align-item-bottom-line', 1, 3, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '数据分析');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 1, 'Ecommerce', 'ecommerce', '/dashboard/ecommerce', 'menus.dashboard.ecommerce', 'ri:bar-chart-box-line', 1, 2, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '电商数据');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 0, 'System', '/system', '/index/index', 'menus.system.title', 'ri:settings-3-line', 0, 2, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '系统管理目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 5, 'User', 'user', '/system/user', 'menus.system.user', 'ri:user-line', 1, 1, 'system.ui.user.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '用户管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 5, 'Role', 'role', '/system/role', 'menus.system.role', 'ri:user-settings-line', 1, 2, 'system.ui.role.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '角色管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 5, 'UserCenter', 'user-center', '/system/user-center', 'menus.system.userCenter', 'ri:user-line', 1, 11, NULL, 1, 1, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '个人中心（隐藏）');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, 5, 'Menus', 'menu', '/system/menu', 'menus.system.menu', 'ri:menu-search-line', 1, 7, 'system.ui.menu.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '菜单管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (49, 0, 'Document', '', '', 'menus.document.title', 'ri:bill-line', 1, 5, '', 0, 0, 0, 0, 'https://doc.framework.junoyi.com/doc/what-is-junoyi', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 16:58:58', '文档外链');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (50, 0, 'ChangeLog', '/change/log', '/change/log', 'menus.plan.log', 'ri:gamepad-line', 1, 6, '', 0, 0, 1, 0, '', 0, 0, '', 0, 'v0.4.0-alpha', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-20 17:05:13', '更新日志');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (93, 0, 'Monitor', '/monitor', '/index/index', 'menus.monitor.title', 'mdi:monitor-dashboard', 0, 3, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (94, 93, 'Cache', 'cache', '/system/cache', 'menus.monitor.cache', 'simple-icons:redis', 1, 2, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (95, 0, 'Operation', '/operation', '/index/index', 'menus.operation.title', 'mdi:toolbox', 0, 4, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (96, 95, 'Generate', 'generate', '/operation/generate', 'menus.operation.generate', 'mdi:robot-outline', 1, 1, '', 1, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (98, 5, 'Task', 'task', '/system/task', 'menus.system.task', 'ep:timer', 1, 9, 'system.ui.task.view', 1, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (101, 5, 'PermissionGroup', 'group', '/system/permission', 'menus.system.permissionGroup', 'ri:folder-lock-line', 1, 4, 'system.ui.permission.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2025-12-31 19:02:19', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (102, 5, 'Department', 'department', '/system/department', 'menus.system.dept', 'ri:building-2-line', 1, 3, 'system.ui.dept.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2025-12-31 19:05:34', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (111, 95, 'Api-doc', 'api-doc', '', 'menus.operation.api-doc', 'ri:braces-fill', 1, 2, '', 0, 0, 1, 0, 'http://localhost:7588/doc.html', 0, 0, '', 0, '', 1, NULL, '2026-01-01 16:06:17', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (115, 93, 'Session', 'session', '/system/session', 'menus.monitor.session', 'ri:chat-settings-line', 1, 1, 'system.ui.session.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-05 16:28:14', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (116, 5, 'PermissionPool', 'permission-pool', '/system/permission-pool', 'menus.system.permissionPool', '', 1, 5, 'system.ui.permission.pool.view', 1, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-07 16:43:49', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (118, 93, 'System-Info', 'info', '/system/info', '系统信息', 'ri:information-2-line', 1, 3, 'system.ui.info.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 18:50:03', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (119, 122, 'AuthLog', 'auth-log', '/system/log/auth', '登录日志', 'ri:book-ai-line', 1, 1, 'system.ui.authlog.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 18:53:23', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (120, 122, 'OperLog', 'oper-log', '/system/log/oper', '操作日志', 'ri:book-read-line', 1, 2, 'system.ui.oper-log.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 18:54:15', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (121, 5, 'Dictionary', 'dict', '/system/dict', '字典管理', 'ri:book-2-fill', 1, 6, 'system.ui.dict.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 19:02:45', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (122, 5, 'LogManage', 'log', '', '日志管理', 'ri:survey-line', 0, 10, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 19:07:31', 'super_admin', '2026-01-20 16:58:58', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (123, 5, 'File', 'file', '/system/file', '文件管理', 'ri:file-ai-2-line', 1, 8, 'system.ui.file.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-20 16:58:24', 'super_admin', '2026-01-20 16:58:58', '');
COMMIT;

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_perm_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_perm_group`;
CREATE TABLE `sys_perm_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_code` varchar(50) DEFAULT NULL COMMENT '权限组编码',
  `group_name` varchar(50) DEFAULT NULL COMMENT '权限组名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父权限组（支持继承）',
  `priority` int DEFAULT NULL COMMENT '优先级（数值越大优先级越高）',
  `description` varchar(500) DEFAULT NULL COMMENT '权限组描述',
  `status` int DEFAULT '1' COMMENT '状态（0停用，1启用）',
  `permissions` text COMMENT '权限列表',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(5000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限组表';

-- ----------------------------
-- Records of sys_perm_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_perm_group` (`id`, `group_code`, `group_name`, `parent_id`, `priority`, `description`, `status`, `permissions`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'default_user', '默认用户权限组', NULL, 10, '系统所有登录用户的基础权限', 1, '[]', 'system', '2025-12-29 21:02:33', 'super_admin', '2026-01-08 19:38:18', '默认用户权限组');
INSERT INTO `sys_perm_group` (`id`, `group_code`, `group_name`, `parent_id`, `priority`, `description`, `status`, `permissions`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'default_admin', '默认管理权限组', 1, 100, '系统所有管理用户的基础权限', 1, '[\"system.ui.user.view\",\"system.ui.user.button.role\"]', 'system', '2025-12-29 21:04:58', 'super_admin', '2026-01-14 16:55:09', '默认管理权限组');
COMMIT;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `permission` varchar(200) DEFAULT NULL COMMENT '权限字符串',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` int DEFAULT '1' COMMENT '状态（1启用，0禁用）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限池（开发期与运维期的权限注册表）';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
BEGIN;
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'system.ui.user.view', '系统用户管理页面权限', 1, 'super_admin', '2026-01-14 16:24:26', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'system.ui.user.button.edit', '系统用户管理用户编辑按钮权限', 1, 'super_admin', '2026-01-14 16:24:48', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 'system.ui.user.button.role', '系统用户管理分配角色按钮权限', 1, 'super_admin', '2026-01-14 16:25:19', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 'system.ui.user.button.dept', '系统用户管理分配部门按钮权限', 1, 'super_admin', '2026-01-14 16:25:43', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 'system.ui.user.button.permission', '系统用户管理分配权限组按钮权限', 1, 'super_admin', '2026-01-14 16:26:13', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 'system.ui.user.button.individual-perm', '系统用户管理分配独立权限按钮权限', 1, 'super_admin', '2026-01-14 16:26:38', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 'system.ui.user.button.delete', '系统用户管理删除按钮权限', 1, 'super_admin', '2026-01-14 16:29:01', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 'system.ui.user.button.add', '系统用户管理添加按钮权限', 1, 'super_admin', '2026-01-14 16:29:32', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, 'system.api.user.get.list', '系统用户管理获取用户列表接口权限', 1, 'super_admin', '2026-01-14 16:31:23', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, 'system.api.user.add', '系统用户管理添加用户接口权限', 1, 'super_admin', '2026-01-14 16:31:59', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, 'system.api.user.update', '系统用户管理更新用户接口权限', 1, 'super_admin', '2026-01-14 16:32:35', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (12, 'system.api.user.delete.id', '系统用户管理删除指定用户接口权限', 1, 'super_admin', '2026-01-14 16:33:33', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (13, 'system.api.user.delete.batch', '系统用户管理批量删除接口权限', 1, 'super_admin', '2026-01-14 16:34:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (14, 'system.api.user.get.roles', '系统用户管理获取用户角色接口权限', 1, 'super_admin', '2026-01-14 16:34:55', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (15, 'system.api.user.update.roles', '系统用户管理绑定角色接口权限', 1, 'super_admin', '2026-01-14 16:35:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (16, 'system.api.user.get.depts', '系统用户管理获取用户部门接口权限', 1, 'super_admin', '2026-01-14 16:36:31', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (17, 'system.api.user.update.depts', '系统用户管理绑定部门接口权限', 1, 'super_admin', '2026-01-14 16:36:57', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (18, 'system.api.user.get.permisson-group', '系统用户管理获取已绑定的权限组接口权限', 1, 'super_admin', '2026-01-14 16:37:56', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (19, 'system.api.user.update.permisson-group', '系统用户管理绑定权限组接口权限', 1, 'super_admin', '2026-01-14 16:38:32', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20, 'system.api.user.get.individual-perm', '系统用户管理获取用户独立权限接口权限', 1, 'super_admin', '2026-01-14 16:40:01', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (21, 'system.api.user.add.individual-perm', '系统用户管理添加用户独立权限接口权限', 1, 'super_admin', '2026-01-14 16:40:41', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (22, 'system.api.user.delete.individual-perm', '系统用户管理删除用户独立权限接口权限', 1, 'super_admin', '2026-01-14 16:41:15', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (23, 'system.ui.role.view', '系统角色管理页面权限', 1, 'super_admin', '2026-01-14 17:03:58', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (24, 'system.ui.role.button.add', '系统角色管理添加按钮权限', 1, 'super_admin', '2026-01-14 17:04:56', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (25, 'system.ui.role.button.edit', '系统角色管理编辑按钮权限', 1, 'super_admin', '2026-01-14 17:05:43', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (26, 'system.ui.role.button.permission', '系统角色管理分配权限组按钮权限', 1, 'super_admin', '2026-01-14 17:06:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (27, 'system.ui.role.button.delete', '系统角色管理删除按钮权限', 1, 'super_admin', '2026-01-14 17:06:33', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (28, 'system.api.role.get.list', '系统角色管理获取角色列表接口权限', 1, 'super_admin', '2026-01-14 17:08:04', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (29, 'system.api.role.get.options', '系统角色管理获取角色下拉选项列表接口权限', 1, 'super_admin', '2026-01-14 17:08:59', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (30, 'system.api.role.get.id', '系统角色管理通过ID获取角色接口权限', 1, 'super_admin', '2026-01-14 17:10:10', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (31, 'system.api.role.add', '系统角色管理添加角色接口权限', 1, 'super_admin', '2026-01-14 17:10:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (32, 'system.api.role.update', '系统角色管理更新角色接口权限', 1, 'super_admin', '2026-01-14 17:11:09', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (33, 'system.api.role.delete.id', '系统角色管理删除角色接口权限', 1, 'super_admin', '2026-01-14 17:11:44', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (34, 'system.api.role.delete.batch', '系统角色管理批量删除角色接口权限', 1, 'super_admin', '2026-01-14 17:12:12', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (35, 'system.api.role.get.permission-group', '系统角色管理获取已绑定的权限组接口权限', 1, 'super_admin', '2026-01-14 17:12:54', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (36, 'system.api.role.update.permission-group', '系统角色管理绑定权限组接口权限', 1, 'super_admin', '2026-01-14 17:13:43', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (37, 'system.ui.dept.view', '系统部门管理页面权限', 1, 'super_admin', '2026-01-14 17:23:13', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (38, 'system.ui.dept.button.add', '系统部门管理添加按钮权限', 1, 'super_admin', '2026-01-14 17:24:09', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (39, 'system.ui.dept.button.edit', '系统部门管理编辑按钮权限', 1, 'super_admin', '2026-01-14 17:24:50', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (40, 'system.ui.dept.button.permission', '系统部门管理分配权限组按钮权限', 1, 'super_admin', '2026-01-14 17:25:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (41, 'system.ui.dept.button.delete', '系统部门管理删除按钮权限', 1, 'super_admin', '2026-01-14 17:25:57', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (42, 'system.api.dept.get.tree', '系统部门管理获取部门树列表接口权限', 1, 'super_admin', '2026-01-14 17:28:03', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (43, 'system.api.dept.get.id', '系统部门管理获取部门接口权限', 1, 'super_admin', '2026-01-14 17:28:37', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (44, 'system.api.dept.add', '系统部门管理添加部门接口权限', 1, 'super_admin', '2026-01-14 17:29:02', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (45, 'system.api.dept.update', '系统部门管理修改部门接口权限', 1, 'super_admin', '2026-01-14 17:29:35', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (46, 'system.api.dept.update.sort', '系统部门管理修改部门排序接口权限', 1, 'super_admin', '2026-01-14 17:30:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (47, 'system.api.dept.delete.id', '系统部门管理删除部门接口权限', 1, 'super_admin', '2026-01-14 17:30:32', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (48, 'system.api.dept.get.permission-group', '系统部门管理获取已绑定的权限组接口权限', 1, 'super_admin', '2026-01-14 17:31:16', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (49, 'system.api.dept.update.permission-group', '系统部门管理绑定权限组接口权限', 1, 'super_admin', '2026-01-14 17:31:51', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (50, 'system.ui.permission.view', '系统权限组管理页面权限', 1, 'super_admin', '2026-01-14 17:42:49', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (51, 'system.ui.permission.button.add', '系统权限组管理添加按钮权限', 1, 'super_admin', '2026-01-14 17:44:15', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (52, 'system.ui.permission.button.edit', '系统权限组管理编辑按钮权限', 1, 'super_admin', '2026-01-14 17:47:56', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (53, 'system.ui.permission.button.delete', '系统权限组管理删除按钮权限', 1, 'super_admin', '2026-01-14 17:48:26', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (54, 'system.api.permission.get.list', '系统权限组管理获取权限组列表接口权限', 1, 'super_admin', '2026-01-14 17:50:19', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (55, 'sytem.api.permission.get.options', '系统权限组管理获取权限组下拉选项列表接口权限', 1, 'super_admin', '2026-01-14 17:52:16', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (56, 'system.api.permission.add', '系统权限组管理添加权限组接口权限', 1, 'super_admin', '2026-01-14 17:52:47', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (57, 'system.api.permission.update', '系统权限组管理更新权限组接口权限', 1, 'super_admin', '2026-01-14 17:53:20', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (58, 'system.api.permission.delete.id', '系统权限组管理删除权限组接口权限', 1, 'super_admin', '2026-01-14 17:56:41', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (59, 'system.api.permission.delete.batch', '系统权限组管理批量删除权限组接口权限', 1, 'super_admin', '2026-01-14 17:57:08', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (60, 'system.ui.permission.pool.view', '系统权限池页面权限', 1, 'super_admin', '2026-01-14 18:42:53', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (61, 'system.ui.permission.pool.button.delete', '系统权限池删除按钮权限', 1, 'super_admin', '2026-01-14 18:43:46', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (62, 'system.ui.permission.pool.button.add', '系统权限池添加按钮权限', 1, 'super_admin', '2026-01-14 18:44:37', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (63, 'system.ui.permission-pool.button.status', '系统权限池状态修改控件权限', 1, 'super_admin', '2026-01-14 18:52:18', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (64, 'system.api.permission.pool.get.list', '系统权限池获取权限列表接口权限', 1, 'super_admin', '2026-01-14 18:53:49', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (65, 'system.api.permission.pool.get.options', '系统权限池获取权限下拉选项列表接口权限', 1, 'super_admin', '2026-01-14 18:54:51', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (66, 'system.api.permission.pool.add', '系统权限池添加权限接口权限', 1, 'super_admin', '2026-01-14 18:55:14', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (67, 'system.api.permission.pool.delete.id', '系统权限池删除权限接口权限', 1, 'super_admin', '2026-01-14 18:55:42', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (68, 'system.api.permission.pool.delete.batch', '系统权限池批量删除权限接口权限', 1, 'super_admin', '2026-01-14 18:56:12', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (69, 'system.api.permission.pool.update.status', '系统权限池更新权限状态接口权限', 1, 'super_admin', '2026-01-14 18:56:49', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (70, 'system.ui.menu.view', '系统菜单管理页面权限', 1, 'super_admin', '2026-01-14 19:09:21', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (71, 'system.ui.menu.button.add', '系统菜单管理添加按钮权限', 1, 'super_admin', '2026-01-14 19:09:51', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (72, 'system.ui.menu.button.edit', '系统菜单管理编辑按钮权限', 1, 'super_admin', '2026-01-14 19:22:32', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (73, 'system.ui.menu.button.delete', '系统菜单管理删除按钮权限', 1, 'super_admin', '2026-01-14 19:23:33', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (74, 'system.api.menu.get.tree', '系统菜单管理获取菜单树列表接口权限', 1, 'super_admin', '2026-01-14 19:27:07', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (75, 'system.api.menu.get.list', '系统菜单管理获取菜单列表接口权限', 1, 'super_admin', '2026-01-14 19:27:38', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (76, 'system.api.menu.get.id', '系统菜单管理获取菜单接口权限', 1, 'super_admin', '2026-01-14 19:28:16', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (77, 'system.api.menu.add', '系统菜单管理添加菜单接口权限', 1, 'super_admin', '2026-01-14 19:28:34', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (78, 'system.api.menu.update', '系统菜单管理修改菜单接口权限', 1, 'super_admin', '2026-01-14 19:29:40', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (79, 'system.api.menu.delete.id', '系统菜单管理删除菜单接口权限', 1, 'super_admin', '2026-01-14 19:30:38', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (80, 'system.api.menu.update.sort', '系统菜单管理菜单排序接口权限', 1, 'super_admin', '2026-01-14 19:32:10', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (81, 'system.ui.session.view', '系统会话监控页面权限', 1, 'super_admin', '2026-01-14 19:53:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (82, 'system.ui.session.button.logout', '系统会话监控会话下线按钮权限', 1, 'super_admin', '2026-01-14 19:54:35', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (83, 'system.api.session.get.list', '系统会话监控获取会话列表接口权限', 1, 'super_admin', '2026-01-14 19:55:50', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (84, 'system.api.session.logout.id', '系统会话监控下线接口权限', 1, 'super_admin', '2026-01-14 19:56:41', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (85, 'system.api.session.logout.batch', '系统会话监控批量下线接口权限', 1, 'super_admin', '2026-01-14 19:57:46', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (86, 'system.ui.cache.view', '系统缓存监控页面权限', 1, 'super_admin', '2026-01-14 20:06:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (87, 'system.ui.cache.button.delete', '系统缓存监控删除按钮权限', 1, 'super_admin', '2026-01-14 20:06:40', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (88, 'system.ui.cache.button.clear', '系统缓存监控清空缓存按钮权限', 1, 'super_admin', '2026-01-14 20:07:08', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (89, 'system.ui.cache.button.detail', '系统缓存监控查看详情按钮权限', 1, 'super_admin', '2026-01-14 20:08:10', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (90, 'system.api.cache.get.info', '系统缓存监控获取redis信息接口权限', 1, 'super_admin', '2026-01-14 20:09:15', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (91, 'system.api.cache.get.keys', '系统缓存监控获取keys接口权限', 1, 'super_admin', '2026-01-14 20:10:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (92, 'system.api.cache.get.key', '系统缓存监控获取key详情接口权限', 1, 'super_admin', '2026-01-14 20:10:33', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (93, 'system.api.cache.delete.key', '系统缓存监控删除指定缓存接口权限', 1, 'super_admin', '2026-01-14 20:11:04', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (94, 'system.api.cache.delete.batch', '系统缓存监控批量删除缓存接口权限', 1, 'super_admin', '2026-01-14 20:11:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (95, 'system.api.cache.clear', '系统缓存监控清空缓存接口权限', 1, 'super_admin', '2026-01-14 20:12:04', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(30) DEFAULT NULL COMMENT '角色名称',
  `role_key` varchar(30) DEFAULT NULL COMMENT '角色关键词',
  `sort` int DEFAULT NULL COMMENT '排序',
  `data_scope` char(1) DEFAULT NULL COMMENT '数据显示范围',
  `status` int DEFAULT '1' COMMENT '状态（1正常，0禁用）',
  `del_flag` tinyint DEFAULT '0' COMMENT '软删除标识符（0正常，1删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` text COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '超级管理员', 'super_admin', 1, '1', 1, 0, 'system', '2025-12-05 08:25:15', 'system', '2025-12-05 08:25:23', '超级管理员');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '管理员', 'admin', 2, '4', 1, 0, 'super_admin', '2025-12-05 08:26:57', 'super_admin', '2026-01-13 17:56:31', '管理员');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '测试角色', 'test_role', 3, '1', 1, 1, 'super_admin', '2026-01-01 16:21:46', 'super_admin', '2026-01-01 16:33:17', '测试用');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '测试角色1', 'test_role1', 1, '1', 1, 1, 'super_admin', '2026-01-01 16:58:32', NULL, NULL, '测试1');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, '测试2', 'test2', 1, '1', 1, 1, 'super_admin', '2026-01-01 16:58:46', NULL, NULL, 'test2');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, '普通用户', 'common', 3, '4', 1, 0, 'super_admin', '2026-01-03 03:44:07', 'super_admin', '2026-01-05 16:19:15', '普通用户');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_group`;
CREATE TABLE `sys_role_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `group_id` bigint DEFAULT NULL COMMENT '权限组ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（临时权限组使用）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-权限组关联表';

-- ----------------------------
-- Records of sys_role_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_group` (`id`, `role_id`, `group_id`, `expire_time`, `create_time`) VALUES (3, 2, 2, NULL, '2026-01-05 16:45:33');
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(100) DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `phonenumber` varchar(11) DEFAULT NULL COMMENT '手机号',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '性别(0 未设定， 1男，2女）',
  `password` varchar(255) DEFAULT NULL COMMENT '密码（加密后）',
  `salt` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '加密盐',
  `status` int DEFAULT '1' COMMENT '状态（1正常，0停用）',
  `del_flag` tinyint DEFAULT '0' COMMENT '软删除标识符号（0未删除，1删除）',
  `pwd_update_time` datetime DEFAULT NULL COMMENT '密码修改时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` text COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`,`del_flag`) COMMENT '用户名唯一索引（配合软删除）',
  UNIQUE KEY `uk_email` (`email`,`del_flag`) COMMENT '邮箱唯一索引（配合软删除）',
  UNIQUE KEY `uk_phonenumber` (`phonenumber`,`del_flag`) COMMENT '手机号唯一索引（配合软删除）'
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'super_admin', '超级管理员', NULL, 'exmple@junoyi.com', '18899887871', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'system', '2025-12-05 08:13:00', 'super_admin', '2026-01-06 15:25:13', '');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'admin', '用户管理员', NULL, 'admin@junoyi.com', '18899887872', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'super_admin', '2025-12-26 08:22:32', 'super_admin', '2026-01-02 22:08:40', '');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 'user1', '钧逸用户1', NULL, 'user1@junoyi.com', '18899887873', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'admin', '2025-12-26 09:02:10', 'admin', '2025-12-26 09:02:15', '钧逸用户');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 'user2', '钧逸用户2', NULL, 'user2@junoyi.com', '18822334454', '0', '2QEBgS3NRYr0BK1IDLbJBeu7N+4a/Dmqt+uk/OfLZKE=', '+/gh5ppNj92gnzr7nK4HpQ==', 1, 0, NULL, 'super_admin', '2026-01-02 20:54:39', 'super_admin', '2026-01-03 05:03:28', '');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 'test1', '测试用户1', NULL, 'test1@junoyi.com', '18866776675', '0', '3ljua1Xq5gFzwIZvOnZFzM0z0q03DsoHCjSyjZ9CUm0=', 'bickL1fDbw3dlFzWEOVahw==', 0, 1, NULL, 'super_admin', '2026-01-03 00:08:38', 'super_admin', '2026-01-03 00:11:02', '测试1');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 'test2', '测试用户2', NULL, 'test2@junoyi.com', '19988776676', '1', '4E2Iup83NXU8WbQaV9VKxzqFt66YkhKix1EkCGLR+Gs=', 'b6+tSxzDuUH4iVzpKuelxg==', 0, 1, NULL, 'super_admin', '2026-01-03 00:09:10', 'super_admin', '2026-01-03 00:11:09', '测试2');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 'test3', '测试用户3', NULL, 'test3@junoyi.com', '18877887676', '0', 'v16QwJwNASg+2zXHOZsD2jo8UewWi0A6xfj9+3TCbgg=', 'Pa8+xWPBWZRbjbn/1Vw7pg==', 0, 1, NULL, 'super_admin', '2026-01-03 00:09:48', 'super_admin', '2026-01-03 00:11:09', '测试用户3');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-部门关联表';

-- ----------------------------
-- Records of sys_user_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (3, 1, 4);
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (4, 1, 5);
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (5, 1, 6);
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (7, 3, 6);
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (8, 4, 6);
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (11, 2, 4);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_group`;
CREATE TABLE `sys_user_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `group_id` bigint NOT NULL COMMENT '权限组ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（支持临时授权）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_group` (`user_id`,`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-权限组关联表';

-- ----------------------------
-- Records of sys_user_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_group` (`id`, `user_id`, `group_id`, `expire_time`, `create_time`) VALUES (2, 3, 1, NULL, '2025-12-29 21:05:28');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_other_auth
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_other_auth`;
CREATE TABLE `sys_user_other_auth` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `auth_type` varchar(20) DEFAULT NULL COMMENT '登录类型',
  `auth_key` varchar(255) DEFAULT NULL COMMENT '平台唯一标识符',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户第三登录绑定表';

-- ----------------------------
-- Records of sys_user_other_auth
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user_perm
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_perm`;
CREATE TABLE `sys_user_perm` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `permission` varchar(200) NOT NULL COMMENT '权限节点',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_perm` (`user_id`,`permission`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户独立权限表';

-- ----------------------------
-- Records of sys_user_perm
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user_platform
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_platform`;
CREATE TABLE `sys_user_platform` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `platform_type` int DEFAULT NULL COMMENT '登录终端平台类型（0后台Web，1前台Web，2小程序，3APP，4桌面端）',
  `platform_uid` varchar(255) DEFAULT NULL COMMENT '平台唯一标识符',
  `login_ip` varchar(128) DEFAULT NULL COMMENT '登录IP',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `token` varchar(255) DEFAULT NULL COMMENT '登录使用到的token',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户登录终端信息表';

-- ----------------------------
-- Records of sys_user_platform
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_platform` (`id`, `user_id`, `platform_type`, `platform_uid`, `login_ip`, `login_time`, `token`) VALUES (1, 1, 0, 'admin_web', '127.0.0.1', '2025-12-18 21:19:35', 'fdafdasfdasfdafdsafdsa');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户角色关联id',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `role_id` bigint DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色数据关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (5, 3, 6);
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (6, 4, 6);
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (27, 1, 1);
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (29, 2, 2);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
