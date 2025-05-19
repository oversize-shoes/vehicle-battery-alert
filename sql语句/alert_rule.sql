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

 Date: 19/05/2025 21:19:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alert_rule
-- ----------------------------
DROP TABLE IF EXISTS `alert_rule`;
CREATE TABLE `alert_rule`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_id` int(11) NOT NULL COMMENT '规则编号，表示一类规则，如1为电压差规则',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则名称，如电压差报警',
  `battery_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '电池类型，如三元电池',
  `level` int(11) NOT NULL COMMENT '报警等级（0最高）',
  `min_value` decimal(10, 2) NOT NULL COMMENT '区间下限',
  `max_value` decimal(10, 2) NOT NULL COMMENT '区间上限（闭区间）',
  `unit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '单位，如 V/A',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '是否启用：1启用，0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
