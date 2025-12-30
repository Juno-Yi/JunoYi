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

 Date: 30/12/2025 20:50:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门主键ID',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统部门权限表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门-权限组关联表';

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
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 0, 'Dashboard', '/dashboard', '/index/index', 'menus.dashboard.title', 'ri:pie-chart-line', 0, 1, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '仪表盘目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 1, 'Console', 'console', '/dashboard/console', 'menus.dashboard.console', 'ri:home-smile-2-line', 1, 1, NULL, 0, 0, 0, 0, NULL, 0, 1, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '控制台');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 1, 'Analysis', 'analysis', '/dashboard/analysis', 'menus.dashboard.analysis', 'ri:align-item-bottom-line', 1, 2, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '数据分析');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 1, 'Ecommerce', 'ecommerce', '/dashboard/ecommerce', 'menus.dashboard.ecommerce', 'ri:bar-chart-box-line', 1, 3, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '电商数据');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 0, 'System', '/system', '/index/index', 'menus.system.title', 'ri:settings-3-line', 0, 9, 'system.ui.system.view', 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '系统管理目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 5, 'User', 'user', '/system/user', 'menus.system.user', 'ri:user-line', 1, 1, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '用户管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 5, 'Role', 'role', '/system/role', 'menus.system.role', 'ri:user-settings-line', 1, 2, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '角色管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 5, 'UserCenter', 'user-center', '/system/user-center', 'menus.system.userCenter', 'ri:user-line', 1, 3, '', 1, 1, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '个人中心（隐藏）');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, 5, 'Menus', 'menu', '/system/menu', 'menus.system.menu', 'ri:menu-line', 1, 4, 'system.ui.menu.view', 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '菜单管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (21, 0, 'Article', '/article', '/index/index', 'menus.article.title', 'ri:book-2-line', 0, 8, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '文章管理目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (22, 21, 'ArticleList', 'article-list', '/article/list', 'menus.article.articleList', 'ri:article-line', 1, 1, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '文章列表');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (25, 21, 'ArticleDetail', 'detail/:id', '/article/detail', 'menus.article.articleDetail', NULL, 1, 2, NULL, 1, 0, 1, 0, NULL, 0, 0, '/article/article-list', 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '文章详情（隐藏）');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (26, 21, 'ArticleComment', 'comment', '/article/comment', 'menus.article.comment', 'ri:mail-line', 1, 3, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '评论管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (27, 21, 'ArticlePublish', 'publish', '/article/publish', 'menus.article.articlePublish', 'ri:telegram-2-line', 1, 4, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '发布文章');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (29, 0, 'Examples', '/examples', '/index/index', 'menus.examples.title', 'ri:sparkling-line', 0, 4, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '功能示例目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (42, 29, 'Tabs', 'tabs', '/examples/tabs', 'menus.examples.tabs', 'ri:price-tag-line', 1, 2, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '标签页');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (43, 29, 'TablesBasic', 'tables/basic', '/examples/tables/basic', 'menus.examples.tablesBasic', 'ri:layout-grid-line', 1, 3, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '基础表格');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (44, 29, 'Tables', 'tables', '/examples/tables', 'menus.examples.tables', 'ri:table-3', 1, 4, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '表格');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (45, 29, 'Forms', 'forms', '/examples/forms', 'menus.examples.forms', 'ri:table-view', 1, 5, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '表单');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (46, 29, 'SearchBar', 'form/search-bar', '/examples/forms/search-bar', 'menus.examples.searchBar', 'ri:table-line', 1, 6, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '搜索栏');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (47, 29, 'TablesTree', 'tables/tree', '/examples/tables/tree', 'menus.examples.tablesTree', 'ri:layout-2-line', 1, 7, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '树形表格');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (48, 29, 'SocketChat', 'socket-chat', '/examples/socket-chat', 'menus.examples.socketChat', 'ri:shake-hands-line', 1, 8, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, 'New', 1, 'system', '2025-12-30 15:29:16', NULL, NULL, 'Socket聊天');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (49, 0, 'Document', '', '', 'menus.help.document', 'ri:bill-line', 1, 10, NULL, 0, 0, 1, 0, 'https://junoyi.eatfan.top', 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '文档外链');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (50, 0, 'ChangeLog', '/change/log', '/change/log', 'menus.plan.log', 'ri:gamepad-line', 1, 11, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, 'v0.1.2', 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '更新日志');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (51, 0, 'Template', '/template', '/index/index', 'menus.template.title', 'ri:apps-2-line', 0, 2, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '模板页面目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (52, 51, 'Cards', 'cards', '/template/cards', 'menus.template.cards', 'ri:wallet-line', 1, 1, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '卡片');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (53, 51, 'Banners', 'banners', '/template/banners', 'menus.template.banners', 'ri:rectangle-line', 1, 2, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '横幅');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (54, 51, 'Charts', 'charts', '/template/charts', 'menus.template.charts', 'ri:bar-chart-box-line', 1, 3, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '图表');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (55, 51, 'Map', 'map', '/template/map', 'menus.template.map', 'ri:map-pin-line', 1, 4, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '地图');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (56, 51, 'Chat', 'chat', '/template/chat', 'menus.template.chat', 'ri:message-3-line', 1, 5, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '聊天');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (57, 51, 'Calendar', 'calendar', '/template/calendar', 'menus.template.calendar', 'ri:calendar-2-line', 1, 6, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '日历');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (58, 51, 'Pricing', 'pricing', '/template/pricing', 'menus.template.pricing', 'ri:money-cny-box-line', 1, 7, NULL, 0, 0, 1, 0, NULL, 1, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '定价');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (59, 0, 'Widgets', '/widgets', '/index/index', 'menus.widgets.title', 'ri:apps-2-add-line', 0, 3, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '组件目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (60, 59, 'Icon', 'icon', '/widgets/icon', 'menus.widgets.icon', 'ri:palette-line', 1, 1, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '图标');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (61, 59, 'ImageCrop', 'image-crop', '/widgets/image-crop', 'menus.widgets.imageCrop', 'ri:screenshot-line', 1, 2, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '图片裁剪');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (62, 59, 'Excel', 'excel', '/widgets/excel', 'menus.widgets.excel', 'ri:download-2-line', 1, 3, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, 'Excel');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (63, 59, 'Video', 'video', '/widgets/video', 'menus.widgets.video', 'ri:vidicon-line', 1, 4, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '视频');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (64, 59, 'CountTo', 'count-to', '/widgets/count-to', 'menus.widgets.countTo', 'ri:anthropic-line', 1, 5, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '数字动画');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (65, 59, 'WangEditor', 'wang-editor', '/widgets/wang-editor', 'menus.widgets.wangEditor', 'ri:t-box-line', 1, 6, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '富文本编辑器');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (66, 59, 'Watermark', 'watermark', '/widgets/watermark', 'menus.widgets.watermark', 'ri:water-flash-line', 1, 7, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '水印');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (67, 59, 'ContextMenu', 'context-menu', '/widgets/context-menu', 'menus.widgets.contextMenu', 'ri:menu-2-line', 1, 8, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '右键菜单');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (68, 59, 'Qrcode', 'qrcode', '/widgets/qrcode', 'menus.widgets.qrcode', 'ri:qr-code-line', 1, 9, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '二维码');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (69, 59, 'Drag', 'drag', '/widgets/drag', 'menus.widgets.drag', 'ri:drag-move-fill', 1, 10, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '拖拽');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (70, 59, 'TextScroll', 'text-scroll', '/widgets/text-scroll', 'menus.widgets.textScroll', 'ri:input-method-line', 1, 11, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '文字滚动');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (71, 59, 'Fireworks', 'fireworks', '/widgets/fireworks', 'menus.widgets.fireworks', 'ri:magic-line', 1, 12, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, 'Hot', 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '烟花');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (72, 59, 'ElementUI', '/outside/iframe/elementui', '', 'menus.widgets.elementUI', 'ri:apps-2-line', 1, 13, NULL, 0, 0, 0, 1, 'https://element-plus.org/zh-CN/component/overview.html', 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, 'ElementUI文档(iframe)');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (73, 0, 'Result', '/result', '/index/index', 'menus.result.title', 'ri:checkbox-circle-line', 0, 5, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '结果页面目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (74, 73, 'ResultSuccess', 'success', '/result/success', 'menus.result.success', 'ri:checkbox-circle-line', 1, 1, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '成功页');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (75, 73, 'ResultFail', 'fail', '/result/fail', 'menus.result.fail', 'ri:close-circle-line', 1, 2, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '失败页');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (76, 0, 'Exception', '/exception', '/index/index', 'menus.exception.title', 'ri:error-warning-line', 0, 6, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '异常页面目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (77, 76, 'Exception403', '403', '/exception/403', 'menus.exception.forbidden', NULL, 1, 1, NULL, 0, 1, 1, 0, NULL, 1, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '403禁止访问');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (78, 76, 'Exception404', '404', '/exception/404', 'menus.exception.notFound', NULL, 1, 2, NULL, 0, 1, 1, 0, NULL, 1, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '404页面不存在');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (79, 76, 'Exception500', '500', '/exception/500', 'menus.exception.serverError', NULL, 1, 3, NULL, 0, 1, 1, 0, NULL, 1, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '500服务器错误');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (81, 80, 'SafeguardServer', 'server', '/safeguard/server', 'menus.safeguard.server', 'ri:hard-drive-3-line', 1, 1, NULL, 0, 0, 1, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', NULL, NULL, '服务器监控');
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
  `permissions` json DEFAULT NULL COMMENT '权限列表',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(5000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限组表';

-- ----------------------------
-- Records of sys_perm_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_perm_group` (`id`, `group_code`, `group_name`, `parent_id`, `priority`, `description`, `status`, `permissions`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'default_user', '默认用户权限组', NULL, 10, '系统所有登录用户的基础权限', 1, '[\"system.ui.dashboard.view\"]', 'system', '2025-12-29 21:02:33', NULL, NULL, '默认用户权限组');
INSERT INTO `sys_perm_group` (`id`, `group_code`, `group_name`, `parent_id`, `priority`, `description`, `status`, `permissions`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'default_admin', '默认管理权限组', 1, 100, '系统所有管理用户的基础权限', 1, '[\"system.ui.menu.view\"]', 'system', '2025-12-29 21:04:58', NULL, NULL, '默认管理权限组');
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '超级管理员', 'super_admin', 1, '1', 1, 0, 'system', '2025-12-05 08:25:15', 'system', '2025-12-05 08:25:23', '超级管理员');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '管理员', 'admin', 2, '2', 1, 0, 'super_admin', '2025-12-05 08:26:57', 'super_admin', '2025-12-05 08:27:08', '管理员');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-权限组关联表';

-- ----------------------------
-- Records of sys_role_group
-- ----------------------------
BEGIN;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'super_admin', '超级管理员', NULL, 'exmple@junoyi.com', '18899887878', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'system', '2025-12-05 08:13:00', 'system', '2025-12-05 08:13:17', '超级管理员');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'admin', '用户管理员', NULL, 'admin@junoyi.com', '18899887877', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'super_admin', '2025-12-26 08:22:32', 'super_admin', '2025-12-26 08:22:43', '用户管理员');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 'user1', '钧逸用户1', NULL, 'user1@junoyi.com', '18899887876', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'admin', '2025-12-26 09:02:10', 'admin', '2025-12-26 09:02:15', '钧逸用户');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-部门关联表';

-- ----------------------------
-- Records of sys_user_dept
-- ----------------------------
BEGIN;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-权限组关联表';

-- ----------------------------
-- Records of sys_user_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_group` (`id`, `user_id`, `group_id`, `expire_time`, `create_time`) VALUES (1, 2, 2, NULL, '2025-12-29 21:03:22');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户独立权限表';

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色数据关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (1, 1, 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
