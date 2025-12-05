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

 Date: 05/12/2025 08:32:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '超级管理员', 'super_admin', 1, '1', 1, 0, 'system', '2025-12-05 08:25:15', 'system', '2025-12-05 08:25:23', '超级管理员');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '管理员', 'admin', 2, '2', 1, 0, 'super_admin', '2025-12-05 08:26:57', 'super_admin', '2025-12-05 08:27:08', '管理员');
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小程序绑定id',
  `user_name` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(100) DEFAULT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户类型（00系统用户， 01 系统注册用户，02小程序用户',
  `emaili` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `phonenumber` varchar(11) DEFAULT NULL COMMENT '手机号',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '性别(0 未设定， 1男，2女）',
  `password` varchar(255) DEFAULT NULL COMMENT '密码（加密后）',
  `salt` varchar(30) DEFAULT NULL COMMENT '加密盐',
  `stauts` int DEFAULT '1' COMMENT '状态（1正常，0停用）',
  `del_flag` tinyint DEFAULT '0' COMMENT '软删除标识符号（0未删除，1删除）',
  `login_ip` varchar(128) DEFAULT NULL COMMENT '登录ip记录',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `pwd_update_time` datetime DEFAULT NULL COMMENT '密码修改时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` text COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`user_id`, `openid`, `user_name`, `nick_name`, `user_type`, `emaili`, `phonenumber`, `sex`, `password`, `salt`, `stauts`, `del_flag`, `login_ip`, `login_time`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, NULL, 'super_admin', '超级管理员', '00', 'exmple@junoyi.com', '18899887878', '1', '29c67a30398638269fe600f73a054934', '111111', 1, 0, '127.0.0.1', '2025-12-05 08:12:41', NULL, 'system', '2025-12-05 08:13:00', 'system', '2025-12-05 08:13:17', '超级管理员');
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (1, 1, 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
