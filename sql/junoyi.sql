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

 Date: 14/01/2026 16:06:19
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
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 0, 'Dashboard', '/dashboard', '/index/index', 'menus.dashboard.title', 'ri:pie-chart-line', 0, 1, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '仪表盘目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 1, 'Console', 'console', '/dashboard/console', 'menus.dashboard.console', 'ri:home-smile-2-line', 1, 1, NULL, 0, 0, 0, 0, NULL, 0, 1, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '控制台');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 1, 'Analysis', 'analysis', '/dashboard/analysis', 'menus.dashboard.analysis', 'ri:align-item-bottom-line', 1, 3, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '数据分析');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 1, 'Ecommerce', 'ecommerce', '/dashboard/ecommerce', 'menus.dashboard.ecommerce', 'ri:bar-chart-box-line', 1, 2, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '电商数据');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 0, 'System', '/system', '/index/index', 'menus.system.title', 'ri:settings-3-line', 0, 3, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '系统管理目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 5, 'User', 'user', '/system/user', 'menus.system.user', 'ri:user-line', 1, 1, 'system.ui.user.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '用户管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 5, 'Role', 'role', '/system/role', 'menus.system.role', 'ri:user-settings-line', 1, 2, 'system.ui.role.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '角色管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 5, 'UserCenter', 'user-center', '/system/user-center', 'menus.system.userCenter', 'ri:user-line', 1, 9, NULL, 1, 1, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '个人中心（隐藏）');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, 5, 'Menus', 'menu', '/system/menu', 'menus.system.menu', 'ri:menu-search-line', 1, 6, 'system.ui.menu.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '菜单管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (21, 51, 'Article', 'article', '', 'menus.article.title', 'ri:book-2-line', 0, 11, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '文章管理目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (22, 21, 'ArticleList', 'article-list', '/article/list', 'menus.article.articleList', 'ri:article-line', 1, 1, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '文章列表');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (25, 21, 'ArticleDetail', 'detail/:id', '/article/detail', 'menus.article.articleDetail', NULL, 1, 2, NULL, 1, 0, 1, 0, NULL, 0, 0, '/article/article-list', 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '文章详情（隐藏）');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (26, 21, 'ArticleComment', 'comment', '/article/comment', 'menus.article.comment', 'ri:mail-line', 1, 3, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '评论管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (27, 21, 'ArticlePublish', 'publish', '/article/publish', 'menus.article.articlePublish', 'ri:telegram-2-line', 1, 4, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '发布文章');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (29, 51, 'Examples', 'examples', '', 'menus.examples.title', 'ri:sparkling-line', 0, 9, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '功能示例目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (42, 29, 'Tabs', 'tabs', '/examples/tabs', 'menus.examples.tabs', 'ri:price-tag-line', 1, 1, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '标签页');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (43, 29, 'TablesBasic', 'tables/basic', '/examples/tables/basic', 'menus.examples.tablesBasic', 'ri:layout-grid-line', 1, 2, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '基础表格');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (44, 29, 'Tables', 'tables', '/examples/tables', 'menus.examples.tables', 'ri:table-3', 1, 3, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '表格');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (45, 29, 'Forms', 'forms', '/examples/forms', 'menus.examples.forms', 'ri:table-view', 1, 4, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '表单');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (46, 29, 'SearchBar', 'form/search-bar', '/examples/forms/search-bar', 'menus.examples.searchBar', 'ri:table-line', 1, 5, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '搜索栏');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (47, 29, 'TablesTree', 'tables/tree', '/examples/tables/tree', 'menus.examples.tablesTree', 'ri:layout-2-line', 1, 6, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '树形表格');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (48, 29, 'SocketChat', 'socket-chat', '/examples/socket-chat', 'menus.examples.socketChat', 'ri:shake-hands-line', 1, 7, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, 'New', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', 'Socket聊天');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (49, 0, 'Document', '', '', 'menus.document.title', 'ri:bill-line', 1, 6, '', 0, 0, 0, 0, 'https://doc.framework.junoyi.com/doc/what-is-junoyi', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-14 14:13:09', '文档外链');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (50, 0, 'ChangeLog', '/change/log', '/change/log', 'menus.plan.log', 'ri:gamepad-line', 1, 7, '', 0, 0, 1, 0, '', 0, 0, '', 0, 'v0.3.0-alpha', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-12 20:40:19', '更新日志');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (51, 0, 'Template', '/template', '/index/index', 'menus.template.title', 'ri:dashboard-line', 0, 2, '', 0, 0, 0, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '模板页面目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (52, 51, 'Cards', 'cards', '/template/cards', 'menus.template.cards', 'ri:wallet-line', 1, 1, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '卡片');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (53, 51, 'Banners', 'banners', '/template/banners', 'menus.template.banners', 'ri:rectangle-line', 1, 2, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '横幅');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (54, 51, 'Charts', 'charts', '/template/charts', 'menus.template.charts', 'ri:bar-chart-box-line', 1, 3, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '图表');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (55, 51, 'Map', 'map', '/template/map', 'menus.template.map', 'ri:map-pin-line', 1, 4, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '地图');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (56, 51, 'Chat', 'chat', '/template/chat', 'menus.template.chat', 'ri:message-3-line', 1, 5, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '聊天');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (57, 51, 'Calendar', 'calendar', '/template/calendar', 'menus.template.calendar', 'ri:calendar-2-line', 1, 6, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '日历');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (58, 51, 'Pricing', 'pricing', '/template/pricing', 'menus.template.pricing', 'ri:money-cny-box-line', 1, 7, NULL, 0, 0, 1, 0, NULL, 1, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '定价');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (59, 51, 'Widgets', 'widgets', '', 'menus.widgets.title', 'ri:apps-2-add-line', 0, 8, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '组件目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (60, 59, 'Icon', 'icon', '/widgets/icon', 'menus.widgets.icon', 'ri:palette-line', 1, 1, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '图标');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (61, 59, 'ImageCrop', 'image-crop', '/widgets/image-crop', 'menus.widgets.imageCrop', 'ri:screenshot-line', 1, 2, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '图片裁剪');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (62, 59, 'Excel', 'excel', '/widgets/excel', 'menus.widgets.excel', 'ri:download-2-line', 1, 3, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', 'Excel');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (63, 59, 'Video', 'video', '/widgets/video', 'menus.widgets.video', 'ri:vidicon-line', 1, 4, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '视频');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (64, 59, 'CountTo', 'count-to', '/widgets/count-to', 'menus.widgets.countTo', 'ri:anthropic-line', 1, 5, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '数字动画');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (65, 59, 'WangEditor', 'wang-editor', '/widgets/wang-editor', 'menus.widgets.wangEditor', 'ri:t-box-line', 1, 6, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '富文本编辑器');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (66, 59, 'Watermark', 'watermark', '/widgets/watermark', 'menus.widgets.watermark', 'ri:water-flash-line', 1, 7, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '水印');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (67, 59, 'ContextMenu', 'context-menu', '/widgets/context-menu', 'menus.widgets.contextMenu', 'ri:menu-2-line', 1, 8, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '右键菜单');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (68, 59, 'Qrcode', 'qrcode', '/widgets/qrcode', 'menus.widgets.qrcode', 'ri:qr-code-line', 1, 9, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '二维码');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (69, 59, 'Drag', 'drag', '/widgets/drag', 'menus.widgets.drag', 'ri:drag-move-fill', 1, 10, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '拖拽');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (70, 59, 'TextScroll', 'text-scroll', '/widgets/text-scroll', 'menus.widgets.textScroll', 'ri:input-method-line', 1, 11, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '文字滚动');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (71, 59, 'Fireworks', 'fireworks', '/widgets/fireworks', 'menus.widgets.fireworks', 'ri:magic-line', 1, 12, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, 'Hot', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '烟花');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (72, 59, 'ElementUI', '/outside/iframe/elementui', '', 'menus.widgets.elementUI', 'ri:apps-2-line', 1, 13, NULL, 0, 0, 0, 1, 'https://element-plus.org/zh-CN/component/overview.html', 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', 'ElementUI文档(iframe)');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (73, 51, 'Result', 'result', '', 'menus.result.title', 'ri:checkbox-circle-line', 0, 10, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '结果页面目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (74, 73, 'ResultSuccess', 'success', '/result/success', 'menus.result.success', 'ri:checkbox-circle-line', 1, 1, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '成功页');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (75, 73, 'ResultFail', 'fail', '/result/fail', 'menus.result.fail', 'ri:close-circle-line', 1, 2, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-01-11 18:26:36', '失败页');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (93, 0, 'Monitor', '/monitor', '/index/index', 'menus.monitor.title', 'mdi:monitor-dashboard', 0, 4, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-11 18:26:36', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (94, 93, 'Cache', 'cache', '/system/cache', 'menus.monitor.cache', 'simple-icons:redis', 1, 2, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-11 18:26:36', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (95, 0, 'Operation', '/operation', '/index/index', 'menus.operation.title', 'mdi:toolbox', 0, 5, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-11 18:26:36', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (96, 95, 'Generate', 'generate', '/operation/generate', 'menus.operation.generate', 'mdi:robot-outline', 1, 1, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-11 18:26:36', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (98, 5, 'Task', 'task', '/system/task', 'menus.system.task', 'ep:timer', 1, 8, 'system.ui.task.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-01-11 18:26:36', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (101, 5, 'PermissionGroup', 'group', '/system/permission', 'menus.system.permissionGroup', 'ri:folder-lock-line', 1, 4, 'system.ui.permission.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2025-12-31 19:02:19', 'super_admin', '2026-01-11 18:26:36', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (102, 5, 'Department', 'department', '/system/department', 'menus.system.dept', 'ri:building-2-line', 1, 3, 'system.ui.dept.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2025-12-31 19:05:34', 'super_admin', '2026-01-11 18:26:36', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (111, 95, 'Api-doc', 'api-doc', '', 'menus.operation.api-doc', 'ri:braces-fill', 1, 2, '', 0, 0, 1, 0, 'http://localhost:7588/doc.html', 0, 0, '', 0, '', 1, NULL, '2026-01-01 16:06:17', 'super_admin', '2026-01-14 14:15:28', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (115, 93, 'Session', 'session', '/system/session', 'menus.monitor.session', 'ri:chat-settings-line', 1, 1, 'system.ui.session.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-05 16:28:14', 'super_admin', '2026-01-11 18:26:36', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (116, 5, 'PermissionPool', 'permission-pool', '/system/permission-pool', 'menus.system.permissionPool', '', 1, 5, 'system.ui.permission.pool.view', 1, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-07 16:43:49', 'super_admin', '2026-01-11 18:26:36', '');
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
INSERT INTO `sys_perm_group` (`id`, `group_code`, `group_name`, `parent_id`, `priority`, `description`, `status`, `permissions`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'default_admin', '默认管理权限组', 1, 100, '系统所有管理用户的基础权限', 1, '[\"system.ui.permission.pool.view\",\"system.ui.role.view\",\"system.ui.task.view\",\"system.ui.user.view\",\"system.ui.menu.view\",\"system.ui.session.view\",\"system.ui.permission.view\",\"system.ui.dept.view\"]', 'system', '2025-12-29 21:04:58', 'super_admin', '2026-01-09 19:13:16', '默认管理权限组');
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
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限池（开发期与运维期的权限注册表）';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
BEGIN;
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'system.ui.menu.view', '系统菜单页面权限', 1, 'super_admin', '2026-01-08 16:40:40', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, 'system.ui.user.view', '系统用户管理页面权限', 1, 'super_admin', '2026-01-08 19:16:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, 'system.ui.role.view', '系统角色管理页面权限', 1, 'super_admin', '2026-01-08 19:16:56', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (12, 'system.ui.dept.view', '系统部门管理页面权限', 1, 'super_admin', '2026-01-08 19:17:40', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (13, 'system.ui.permission.view', '系统权限管理页面权限', 1, 'super_admin', '2026-01-08 19:18:03', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (15, 'system.ui.task.view', '系统定时任务页面权限', 1, 'super_admin', '2026-01-08 19:19:19', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (16, 'system.ui.permission.pool.view', '系统权限池管理页面权限', 1, 'super_admin', '2026-01-08 19:53:17', 'super_admin', '2026-01-08 20:42:47', NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (18, 'system.ui.session.view', '系统会话管理页面权限', 1, 'super_admin', '2026-01-09 19:08:26', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (19, 'system.ui.session.button.logout', '系统会话管理强制下线按钮权限', 1, 'super_admin', '2026-01-09 19:12:07', NULL, NULL, NULL);
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
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'super_admin', '超级管理员', NULL, 'exmple@junoyi.com', '18899887876', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'system', '2025-12-05 08:13:00', 'super_admin', '2026-01-06 15:25:13', '');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'admin', '用户管理员', NULL, 'admin@junoyi.com', '18899887877', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'super_admin', '2025-12-26 08:22:32', 'super_admin', '2026-01-02 22:08:40', '');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 'user1', '钧逸用户1', NULL, 'user1@junoyi.com', '18899887876', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'admin', '2025-12-26 09:02:10', 'admin', '2025-12-26 09:02:15', '钧逸用户');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 'user2', '钧逸用户2', NULL, 'user2@junoyi.com', '18822334455', '0', '2QEBgS3NRYr0BK1IDLbJBeu7N+4a/Dmqt+uk/OfLZKE=', '+/gh5ppNj92gnzr7nK4HpQ==', 1, 0, NULL, 'super_admin', '2026-01-02 20:54:39', 'super_admin', '2026-01-03 05:03:28', '');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 'test1', '测试用户1', NULL, 'test1@junoyi.com', '18866776677', '0', '3ljua1Xq5gFzwIZvOnZFzM0z0q03DsoHCjSyjZ9CUm0=', 'bickL1fDbw3dlFzWEOVahw==', 0, 1, NULL, 'super_admin', '2026-01-03 00:08:38', 'super_admin', '2026-01-03 00:11:02', '测试1');
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
