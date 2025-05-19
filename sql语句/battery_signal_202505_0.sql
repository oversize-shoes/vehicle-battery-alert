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

 Date: 19/05/2025 21:23:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for battery_signal_202505_0
-- ----------------------------
DROP TABLE IF EXISTS `battery_signal_202505_0`;
CREATE TABLE `battery_signal_202505_0`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `vin` bigint(20) NULL DEFAULT NULL,
  `signal_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '信号类型',
  `signal_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `report_time` datetime NOT NULL COMMENT '信号上报时间',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `version` int(11) NULL DEFAULT 0 COMMENT '乐观锁版本号',
  `uuid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '幂等标识',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_vid_time`(`vin`, `report_time`) USING BTREE,
  INDEX `idx_signal_type`(`signal_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
