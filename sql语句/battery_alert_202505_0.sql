/*
 Navicat Premium Dump SQL

 Source Server         : MySQL57
 Source Server Type    : MySQL
 Source Server Version : 50709 (5.7.9)
 Source Host           : localhost:3309
 Source Schema         : vehicle_0

 Target Server Type    : MySQL
 Target Server Version : 50709 (5.7.9)
 File Encoding         : 65001

 Date: 19/05/2025 21:21:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for battery_alert_202505_0
-- ----------------------------
DROP TABLE IF EXISTS `battery_alert_202505_0`;
CREATE TABLE `battery_alert_202505_0`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `vin` bigint(20) NULL DEFAULT NULL,
  `rule_id` bigint(20) NOT NULL COMMENT '触发的规则ID',
  `alert_level` tinyint(4) NULL DEFAULT NULL,
  `alert_time` datetime NOT NULL COMMENT '预警时间',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '处理状态 0-未处理 1-已处理',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '告警内容',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_vid_time`(`vin`, `alert_time`) USING BTREE,
  INDEX `idx_rule`(`rule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
