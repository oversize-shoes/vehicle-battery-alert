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

 Date: 19/05/2025 21:24:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for vehicle_0
-- ----------------------------
DROP TABLE IF EXISTS `vehicle_0`;
CREATE TABLE `vehicle_0`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `vid` char(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '车辆识别码，业务唯一',
  `vin` bigint(20) NULL DEFAULT NULL,
  `battery_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `mileage` int(11) NULL DEFAULT 0,
  `health` tinyint(4) NULL DEFAULT NULL,
  `region_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_vid`(`vid`) USING BTREE,
  UNIQUE INDEX `uk_vin`(`vin`) USING BTREE,
  INDEX `idx_region`(`region_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1131260584965177346 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
