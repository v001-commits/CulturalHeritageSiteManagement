/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : project11

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 12/05/2026 21:01:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alert_record
-- ----------------------------
DROP TABLE IF EXISTS `alert_record`;
CREATE TABLE `alert_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `factor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '因子类型',
  `factor_value` decimal(10, 2) NOT NULL COMMENT '因子值',
  `alert_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '告警级别',
  `alert_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '告警信息',
  `suggestion` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '保护建议',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending-待处理，processed-已处理',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_area_time`(`area_id` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '告警记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of alert_record
-- ----------------------------
INSERT INTO `alert_record` VALUES (1, 4, 'temperature', 32.00, 'high', '天坛公园温度超出阈值(32℃)', '建议开启空调降温，加强通风', 'processed', '2026-05-09 14:33:13');
INSERT INTO `alert_record` VALUES (2, 4, 'pm25', 120.00, 'high', '天坛公园PM2.5超标(120μg/m³)', '建议关闭门窗，启动空气净化设备', 'pending', '2026-05-09 14:33:13');
INSERT INTO `alert_record` VALUES (3, 3, 'humidity', 70.00, 'medium', '颐和园湿度偏高(70%)', '及时通风除湿，检查除湿设备运行状态', 'processed', '2026-05-09 13:03:13');

-- ----------------------------
-- Table structure for alert_rule
-- ----------------------------
DROP TABLE IF EXISTS `alert_rule`;
CREATE TABLE `alert_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `factor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '因子类型：temperature-温度，humidity-湿度，pm25-PM2.5',
  `threshold_min` decimal(10, 2) NULL DEFAULT NULL COMMENT '最小阈值',
  `threshold_max` decimal(10, 2) NULL DEFAULT NULL COMMENT '最大阈值',
  `alert_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '告警级别：low-低，medium-中，high-高',
  `enable` tinyint NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '告警规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of alert_rule
-- ----------------------------
INSERT INTO `alert_rule` VALUES (1, 1, 'temperature', 10.00, 30.00, 'medium', 1, '2026-05-09 15:03:13');
INSERT INTO `alert_rule` VALUES (2, 1, 'humidity', 40.00, 70.00, 'medium', 1, '2026-05-09 15:03:13');
INSERT INTO `alert_rule` VALUES (3, 1, 'pm25', NULL, 75.00, 'high', 1, '2026-05-09 15:03:13');
INSERT INTO `alert_rule` VALUES (4, 2, 'temperature', 5.00, 35.00, 'medium', 1, '2026-05-09 15:03:13');
INSERT INTO `alert_rule` VALUES (5, 1, 'temperature', 5.00, 25.00, 'medium', 1, '2026-05-09 18:42:48');
INSERT INTO `alert_rule` VALUES (6, 1, 'humidity', 50.00, 70.00, 'medium', 1, '2026-05-09 18:42:48');
INSERT INTO `alert_rule` VALUES (7, 1, 'so2', NULL, 40.00, 'medium', 1, '2026-05-09 18:42:48');
INSERT INTO `alert_rule` VALUES (8, 1, 'temperature', 10.00, 28.00, 'medium', 1, '2026-05-09 18:42:48');
INSERT INTO `alert_rule` VALUES (9, 1, 'humidity', 40.00, 65.00, 'medium', 1, '2026-05-09 18:42:49');
INSERT INTO `alert_rule` VALUES (10, 1, 'temperature', 0.00, 35.00, 'medium', 1, '2026-05-09 18:42:50');
INSERT INTO `alert_rule` VALUES (11, 1, 'humidity', 30.00, 80.00, 'medium', 1, '2026-05-09 18:42:50');

-- ----------------------------
-- Table structure for environment_data
-- ----------------------------
DROP TABLE IF EXISTS `environment_data`;
CREATE TABLE `environment_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '风险等级：safe-安全，low-低风险，medium-中风险，high-高风险',
  `temperature` decimal(5, 2) NULL DEFAULT NULL COMMENT '温度(℃)',
  `humidity` decimal(5, 2) NULL DEFAULT NULL COMMENT '湿度(%)',
  `pm25` decimal(5, 2) NULL DEFAULT NULL COMMENT 'PM2.5',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '环境监测数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of environment_data
-- ----------------------------
INSERT INTO `environment_data` VALUES (1, 1, 'safe', 22.50, 55.00, 35.00, '2026-05-09 15:03:12');
INSERT INTO `environment_data` VALUES (2, 2, 'low', 25.00, 60.00, 45.00, '2026-05-09 15:03:12');
INSERT INTO `environment_data` VALUES (3, 3, 'medium', 28.00, 70.00, 75.00, '2026-05-09 15:03:12');
INSERT INTO `environment_data` VALUES (4, 4, 'high', 32.00, 80.00, 120.00, '2026-05-09 15:03:12');
INSERT INTO `environment_data` VALUES (5, 5, 'low', 24.00, 58.00, 40.00, '2026-05-09 15:03:12');
INSERT INTO `environment_data` VALUES (6, 6, 'safe', 23.00, 56.00, 38.00, '2026-05-09 15:03:12');

-- ----------------------------
-- Table structure for environment_data_realtime
-- ----------------------------
DROP TABLE IF EXISTS `environment_data_realtime`;
CREATE TABLE `environment_data_realtime`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `device_id` bigint NULL DEFAULT NULL COMMENT '设备ID',
  `temperature` decimal(5, 2) NULL DEFAULT NULL COMMENT '温度(℃)',
  `humidity` decimal(5, 2) NULL DEFAULT NULL COMMENT '湿度(%)',
  `pm25` decimal(8, 2) NULL DEFAULT NULL COMMENT 'PM2.5(μg/m³)',
  `so2` decimal(8, 2) NULL DEFAULT NULL COMMENT 'SO₂(μg/m³)',
  `no2` decimal(8, 2) NULL DEFAULT NULL COMMENT 'NO₂(μg/m³)',
  `wind_speed` decimal(5, 2) NULL DEFAULT NULL COMMENT '风速(m/s)',
  `wind_direction` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风向',
  `light_intensity` decimal(10, 2) NULL DEFAULT NULL COMMENT '光照强度(lux)',
  `soil_moisture` decimal(5, 2) NULL DEFAULT NULL COMMENT '土壤墒情(%)',
  `water_quality` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '水质等级',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'safe' COMMENT '风险等级',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_device_id`(`device_id` ASC) USING BTREE,
  INDEX `idx_record_time`(`record_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '环境数据实时视图' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of environment_data_realtime
-- ----------------------------
INSERT INTO `environment_data_realtime` VALUES (1, 1, 1, 22.50, 55.00, 35.00, 12.00, NULL, 2.50, '东风', NULL, NULL, NULL, 'safe', '2026-05-09 18:05:35', '2026-05-09 18:05:35');
INSERT INTO `environment_data_realtime` VALUES (2, 1, 2, 23.00, 58.00, 45.00, 15.00, NULL, 3.00, '南风', NULL, NULL, NULL, 'low', '2026-05-09 18:05:35', '2026-05-09 18:05:35');
INSERT INTO `environment_data_realtime` VALUES (3, 2, 3, 18.50, 45.00, 28.00, 8.00, NULL, 5.50, '北风', NULL, NULL, NULL, 'safe', '2026-05-09 18:05:35', '2026-05-09 18:05:35');
INSERT INTO `environment_data_realtime` VALUES (4, 3, 4, 20.00, 62.00, 32.00, 10.00, NULL, 1.80, '东南', NULL, NULL, NULL, 'safe', '2026-05-09 18:05:35', '2026-05-09 18:05:35');
INSERT INTO `environment_data_realtime` VALUES (5, 1, 1, 22.50, 55.00, 35.00, 12.00, 18.00, 2.50, '东风', 45000.00, 35.00, '良', 'safe', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (6, 1, 2, 23.00, 58.00, 45.00, 15.00, 20.00, 3.00, '南风', 48000.00, 38.00, '良', 'low', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (7, 1, 3, 22.80, 56.00, 38.00, 13.00, 19.00, 2.80, '东南', 46000.00, 36.00, '良', 'safe', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (8, 2, 4, 18.50, 45.00, 28.00, 8.00, 12.00, 5.50, '北风', 52000.00, 25.00, '优', 'safe', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (9, 2, 5, 18.00, 43.00, 30.00, 9.00, 13.00, 5.80, '北风', 51000.00, 24.00, '优', 'low', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (10, 3, 6, 20.00, 62.00, 32.00, 10.00, 15.00, 1.80, '东南', 44000.00, 55.00, '优', 'safe', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (11, 3, 7, 20.50, 63.00, 33.00, 11.00, 16.00, 2.00, '东南', 45000.00, 56.00, '优', 'safe', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (12, 4, 8, 21.50, 54.00, 36.00, 12.00, 17.00, 2.20, '南风', 47000.00, 40.00, '良', 'safe', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (13, 5, 9, 25.00, 25.00, 55.00, 25.00, 22.00, 4.50, '西风', 68000.00, 15.00, '中', 'medium', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (14, 5, 10, 25.50, 24.00, 58.00, 26.00, 23.00, 4.80, '西风', 69000.00, 14.00, '中', 'medium', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (15, 6, 11, 19.00, 48.00, 42.00, 14.00, 18.00, 3.20, '东风', 50000.00, 32.00, '良', 'low', '2026-05-09 18:30:41', '2026-05-09 18:30:41');
INSERT INTO `environment_data_realtime` VALUES (16, 6, 12, 19.50, 49.00, 40.00, 13.00, 17.00, 3.00, '东风', 49000.00, 33.00, '良', 'safe', '2026-05-09 18:30:41', '2026-05-09 18:30:41');

-- ----------------------------
-- Table structure for file_info
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储文件名',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件类型',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件存储路径',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件访问URL',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'default' COMMENT '所属模块',
  `user_id` bigint NULL DEFAULT NULL COMMENT '上传用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上传用户名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_info
-- ----------------------------

-- ----------------------------
-- Table structure for heritage_zone
-- ----------------------------
DROP TABLE IF EXISTS `heritage_zone`;
CREATE TABLE `heritage_zone`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `zone_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称',
  `zone_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区类型：core-核心保护区，buffer-缓冲区',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分区描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '遗产地分区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of heritage_zone
-- ----------------------------
INSERT INTO `heritage_zone` VALUES (1, 1, '故宫核心区', 'core', '故宫博物院核心保护区域', '2026-05-09 15:03:12');
INSERT INTO `heritage_zone` VALUES (2, 1, '故宫缓冲区', 'buffer', '故宫博物院缓冲区域', '2026-05-09 15:03:12');
INSERT INTO `heritage_zone` VALUES (3, 2, '长城核心段', 'core', '八达岭长城核心保护段', '2026-05-09 15:03:12');
INSERT INTO `heritage_zone` VALUES (4, 2, '长城缓冲区', 'buffer', '八达岭长城缓冲区域', '2026-05-09 15:03:12');
INSERT INTO `heritage_zone` VALUES (5, 3, '颐和园核心区', 'core', '颐和园核心保护区', '2026-05-09 15:03:12');
INSERT INTO `heritage_zone` VALUES (6, 3, '颐和园缓冲区', 'buffer', '颐和园缓冲区', '2026-05-09 15:03:12');

-- ----------------------------
-- Table structure for history_data
-- ----------------------------
DROP TABLE IF EXISTS `history_data`;
CREATE TABLE `history_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `temperature` decimal(5, 2) NULL DEFAULT NULL COMMENT '温度',
  `humidity` decimal(5, 2) NULL DEFAULT NULL COMMENT '湿度',
  `pm25` decimal(5, 2) NULL DEFAULT NULL COMMENT 'PM2.5',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_area_time`(`area_id` ASC, `record_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '历史监测数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of history_data
-- ----------------------------
INSERT INTO `history_data` VALUES (1, 1, 20.50, 52.00, 30.00, '2026-05-02 15:03:12');
INSERT INTO `history_data` VALUES (2, 1, 21.00, 53.00, 32.00, '2026-05-03 15:03:12');
INSERT INTO `history_data` VALUES (3, 1, 21.50, 54.00, 33.00, '2026-05-04 15:03:12');
INSERT INTO `history_data` VALUES (4, 1, 22.00, 54.50, 34.00, '2026-05-05 15:03:12');
INSERT INTO `history_data` VALUES (5, 1, 22.50, 55.00, 35.00, '2026-05-06 15:03:12');
INSERT INTO `history_data` VALUES (6, 2, 23.00, 58.00, 40.00, '2026-05-02 15:03:12');
INSERT INTO `history_data` VALUES (7, 2, 24.00, 59.00, 42.00, '2026-05-03 15:03:12');
INSERT INTO `history_data` VALUES (8, 2, 25.00, 60.00, 45.00, '2026-05-04 15:03:12');

-- ----------------------------
-- Table structure for monitor_area
-- ----------------------------
DROP TABLE IF EXISTS `monitor_area`;
CREATE TABLE `monitor_area`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区域名称',
  `area_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区域编码',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区域描述',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `boundary_points` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '区域边界点位(JSON格式存储经纬度数组)',
  `heritage_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '遗产类型: ancient_building-古建筑, cave-石窟寺, site-遗址, garden-园林',
  `model_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '三维模型URL',
  `camera_position` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '默认相机位置(JSON格式)',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'safe' COMMENT '区域整体风险等级',
  `altitude` decimal(10, 2) NULL DEFAULT NULL COMMENT '区域海拔高度(米)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `area_code`(`area_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '监测区域表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of monitor_area
-- ----------------------------
INSERT INTO `monitor_area` VALUES (1, '故宫博物院', 'AREA001', '北京故宫文化遗产监测区域', 39.916668, 116.397026, '2026-05-09 15:03:12', '[[116.390, 39.920], [116.400, 39.920], [116.400, 39.910], [116.390, 39.910]]', 'ancient_building', NULL, '{\"longitude\": 116.397026, \"latitude\": 39.916345, \"height\": 1000, \"heading\": 0, \"pitch\": -45, \"roll\": 0}', 'safe', 50.00);
INSERT INTO `monitor_area` VALUES (2, '长城八达岭段', 'AREA002', '八达岭长城文化遗产监测区域', 40.359461, 116.007845, '2026-05-09 15:03:12', '[[116.565, 40.365], [116.575, 40.365], [116.575, 40.355], [116.565, 40.355]]', 'site', NULL, '{\"longitude\": 116.570838, \"latitude\": 40.359217, \"height\": 1500, \"heading\": 0, \"pitch\": -40, \"roll\": 0}', 'low', 800.00);
INSERT INTO `monitor_area` VALUES (3, '颐和园', 'AREA003', '颐和园文化遗产监测区域', 39.999718, 116.275246, '2026-05-09 15:03:12', '[[116.270, 40.005], [116.280, 40.005], [116.280, 39.995], [116.270, 39.995]]', 'garden', NULL, '{\"longitude\": 116.275000, \"latitude\": 39.999000, \"height\": 800, \"heading\": 0, \"pitch\": -45, \"roll\": 0}', 'safe', 60.00);
INSERT INTO `monitor_area` VALUES (4, '天坛公园', 'AREA004', '天坛文化遗产监测区域', 39.882826, 116.407394, '2026-05-09 15:03:12', '[[116.402, 39.887], [116.412, 39.887], [116.412, 39.877], [116.402, 39.877]]', 'ancient_building', NULL, '{\"longitude\": 116.407000, \"latitude\": 39.882000, \"height\": 800, \"heading\": 0, \"pitch\": -45, \"roll\": 0}', 'safe', 45.00);
INSERT INTO `monitor_area` VALUES (5, '敦煌莫高窟', 'AREA005', '敦煌莫高窟文化遗产监测区域', 40.040556, 94.803333, '2026-05-09 15:03:12', '[[94.798, 40.045], [94.808, 40.045], [94.808, 40.035], [94.798, 40.035]]', 'cave', NULL, '{\"longitude\": 94.803000, \"latitude\": 40.040000, \"height\": 1500, \"heading\": 0, \"pitch\": -40, \"roll\": 0}', 'medium', 1200.00);
INSERT INTO `monitor_area` VALUES (6, '秦始皇陵', 'AREA006', '秦始皇陵文化遗产监测区域', 34.384444, 109.278611, '2026-05-09 15:03:12', '[[109.268, 34.389], [109.278, 34.389], [109.278, 34.379], [109.268, 34.379]]', 'site', NULL, '{\"longitude\": 109.273000, \"latitude\": 34.384000, \"height\": 1200, \"heading\": 0, \"pitch\": -45, \"roll\": 0}', 'low', 400.00);

-- ----------------------------
-- Table structure for monitor_device
-- ----------------------------
DROP TABLE IF EXISTS `monitor_device`;
CREATE TABLE `monitor_device`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `device_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备名称',
  `device_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备类型',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'online' COMMENT '设备状态：online-在线，offline-离线',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '设备纬度',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '设备经度',
  `altitude` decimal(10, 2) NULL DEFAULT NULL COMMENT '设备海拔高度(米)',
  `photo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备现场照片URL',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'safe' COMMENT '风险等级: safe-安全, low-低风险, medium-中风险, high-高风险',
  `last_data_time` datetime NULL DEFAULT NULL COMMENT '最后数据上报时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '监测设备表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of monitor_device
-- ----------------------------
INSERT INTO `monitor_device` VALUES (1, 1, '故宫温度传感器01', '温度传感器', 'online', '2026-05-09 15:03:12', NULL, NULL, NULL, NULL, 'safe', NULL);
INSERT INTO `monitor_device` VALUES (2, 1, '故宫湿度传感器01', '湿度传感器', 'online', '2026-05-09 15:03:12', NULL, NULL, NULL, NULL, 'safe', NULL);
INSERT INTO `monitor_device` VALUES (3, 2, '长城温度传感器01', '温度传感器', 'online', '2026-05-09 15:03:12', NULL, NULL, NULL, NULL, 'safe', NULL);
INSERT INTO `monitor_device` VALUES (4, 3, '颐和园空气质量监测仪', '空气质量', 'offline', '2026-05-09 15:03:12', NULL, NULL, NULL, NULL, 'safe', NULL);
INSERT INTO `monitor_device` VALUES (5, 4, '天坛温湿度传感器', '温湿度', 'online', '2026-05-09 15:03:12', NULL, NULL, NULL, NULL, 'safe', NULL);
INSERT INTO `monitor_device` VALUES (6, 5, '敦煌光照传感器', '光照传感器', 'online', '2026-05-09 15:03:12', NULL, NULL, NULL, NULL, 'safe', NULL);
INSERT INTO `monitor_device` VALUES (7, 1, '故宫东区温湿度传感器01', '温湿度传感器', 'online', '2026-05-09 18:05:35', 39.916345, 116.397026, 50.00, '/uploads/device_photo_1.jpg', 'safe', '2026-05-09 18:05:35');
INSERT INTO `monitor_device` VALUES (8, 1, '故宫西区空气质量监测站', '空气质量监测', 'online', '2026-05-09 18:05:35', 39.916000, 116.395000, 50.00, '/uploads/device_photo_2.jpg', 'low', '2026-05-09 18:05:35');
INSERT INTO `monitor_device` VALUES (9, 2, '长城八达岭气象站', '气象监测站', 'online', '2026-05-09 18:05:35', 40.359217, 116.570838, 800.00, '/uploads/device_photo_3.jpg', 'safe', '2026-05-09 18:05:35');
INSERT INTO `monitor_device` VALUES (10, 3, '颐和园昆明湖水质监测', '水质监测', 'online', '2026-05-09 18:05:35', 39.990000, 116.275000, 60.00, '/uploads/device_photo_4.jpg', 'safe', '2026-05-09 18:05:35');
INSERT INTO `monitor_device` VALUES (11, 1, '故宫东区温湿度传感器01', '温湿度传感器', 'online', '2026-05-09 18:30:41', 39.916345, 116.397026, 50.00, '/uploads/device_photo_1.jpg', 'safe', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (12, 1, '故宫西区空气质量监测站', '空气质量监测', 'online', '2026-05-09 18:30:41', 39.916000, 116.395000, 50.00, '/uploads/device_photo_2.jpg', 'low', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (13, 1, '故宫南区微气候监测', '微气候监测', 'online', '2026-05-09 18:30:41', 39.915000, 116.397000, 50.00, '/uploads/device_photo_3.jpg', 'safe', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (14, 2, '长城八达岭气象站', '气象监测站', 'online', '2026-05-09 18:30:41', 40.359217, 116.570838, 800.00, '/uploads/device_photo_4.jpg', 'safe', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (15, 2, '长城结构监测01', '结构监测', 'online', '2026-05-09 18:30:41', 40.360000, 116.571000, 800.00, '/uploads/device_photo_5.jpg', 'low', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (16, 3, '颐和园昆明湖水质监测', '水质监测', 'online', '2026-05-09 18:30:41', 39.999000, 116.275000, 60.00, '/uploads/device_photo_6.jpg', 'safe', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (17, 3, '颐和园佛香阁微环境监测', '微环境监测', 'online', '2026-05-09 18:30:41', 40.000000, 116.276000, 80.00, '/uploads/device_photo_7.jpg', 'safe', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (18, 4, '天坛祈年殿环境监测', '环境监测', 'online', '2026-05-09 18:30:41', 39.882000, 116.407000, 45.00, '/uploads/device_photo_8.jpg', 'safe', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (19, 5, '莫高窟洞窟微环境01', '微环境监测', 'online', '2026-05-09 18:30:41', 40.040000, 94.803000, 1200.00, '/uploads/device_photo_9.jpg', 'medium', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (20, 5, '莫高窟空气质量监测', '空气质量监测', 'online', '2026-05-09 18:30:41', 40.041000, 94.804000, 1200.00, '/uploads/device_photo_10.jpg', 'medium', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (21, 6, '兵马俑坑环境监测01', '环境监测', 'online', '2026-05-09 18:30:41', 34.384000, 109.273000, 400.00, '/uploads/device_photo_11.jpg', 'low', '2026-05-09 18:30:41');
INSERT INTO `monitor_device` VALUES (22, 6, '兵马俑坑温湿度监测', '温湿度监测', 'online', '2026-05-09 18:30:41', 34.385000, 109.274000, 400.00, '/uploads/device_photo_12.jpg', 'safe', '2026-05-09 18:30:41');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '操作人ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人用户名',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `operation_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作内容',
  `operation_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作结果：success-成功，fail-失败',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, 1, 'admin', '参数调整', '调整故宫博物院温度阈值：上限30℃，下限10℃', 'success', '192.168.1.100', '2026-05-04 15:03:12');
INSERT INTO `operation_log` VALUES (2, 1, 'admin', '参数调整', '调整长城八达岭段湿度阈值：上限80%，下限40%', 'success', '192.168.1.100', '2026-05-04 15:03:12');
INSERT INTO `operation_log` VALUES (3, 3, 'monitor1', '设备维护', '故宫博物院温度传感器校准', 'success', '192.168.1.101', '2026-05-05 15:03:12');
INSERT INTO `operation_log` VALUES (4, 7, 'researcher1', '报告导出', '导出故宫博物院2024年度监测报告', 'success', '192.168.1.102', '2026-05-06 15:03:12');
INSERT INTO `operation_log` VALUES (5, 1, 'admin', '预警推送', '向遗产地管理员推送高温预警', 'success', '192.168.1.100', '2026-05-06 15:03:12');
INSERT INTO `operation_log` VALUES (6, 8, 'researcher2', '报告导出', '导出天坛公园季度分析报告', 'success', '192.168.1.103', '2026-05-07 15:03:12');
INSERT INTO `operation_log` VALUES (7, 4, 'monitor2', '设备维护', '颐和园湿度传感器更换', 'success', '192.168.1.104', '2026-05-07 15:03:12');
INSERT INTO `operation_log` VALUES (8, 1, 'admin', '参数调整', '调整敦煌莫高窟光照阈值：上限500lux', 'success', '192.168.1.100', '2026-05-08 15:03:12');
INSERT INTO `operation_log` VALUES (9, 9, 'researcher3', '报告导出', '导出秦始皇陵月度监测报告', 'success', '192.168.1.105', '2026-05-08 15:03:12');
INSERT INTO `operation_log` VALUES (10, 1, 'admin', '预警推送', '向监测人员推送设备异常预警', 'success', '192.168.1.100', '2026-05-08 15:03:12');
INSERT INTO `operation_log` VALUES (11, 3, 'monitor1', '数据核验', '核验故宫博物院监测数据', 'success', '192.168.1.101', '2026-05-09 03:03:12');
INSERT INTO `operation_log` VALUES (12, 7, 'researcher1', '报告导出', '导出长城八达岭段周报', 'success', '192.168.1.102', '2026-05-09 09:03:12');
INSERT INTO `operation_log` VALUES (13, 1, 'admin', '参数调整', '调整颐和园CO2阈值：上限1000ppm', 'success', '192.168.1.100', '2026-05-09 12:03:12');
INSERT INTO `operation_log` VALUES (14, 5, 'monitor3', '设备维护', '敦煌莫高窟传感器巡检', 'success', '192.168.1.106', '2026-05-09 13:03:12');
INSERT INTO `operation_log` VALUES (15, 1, 'admin', '预警推送', '向科研人员推送数据异常通知', 'success', '192.168.1.100', '2026-05-09 14:03:12');
INSERT INTO `operation_log` VALUES (16, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 18:00:51');
INSERT INTO `operation_log` VALUES (17, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 18:18:49');
INSERT INTO `operation_log` VALUES (18, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 18:38:11');
INSERT INTO `operation_log` VALUES (19, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 18:57:44');
INSERT INTO `operation_log` VALUES (20, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 18:58:35');
INSERT INTO `operation_log` VALUES (21, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 19:00:06');
INSERT INTO `operation_log` VALUES (22, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 19:02:40');
INSERT INTO `operation_log` VALUES (23, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 19:06:45');
INSERT INTO `operation_log` VALUES (24, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 19:11:40');
INSERT INTO `operation_log` VALUES (25, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 19:19:15');
INSERT INTO `operation_log` VALUES (26, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 20:59:16');
INSERT INTO `operation_log` VALUES (27, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:02:48');
INSERT INTO `operation_log` VALUES (28, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:02:49');
INSERT INTO `operation_log` VALUES (29, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:02:50');
INSERT INTO `operation_log` VALUES (30, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:02:57');
INSERT INTO `operation_log` VALUES (31, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:02:58');
INSERT INTO `operation_log` VALUES (32, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:03:05');
INSERT INTO `operation_log` VALUES (33, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:04:59');
INSERT INTO `operation_log` VALUES (34, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:05:00');
INSERT INTO `operation_log` VALUES (35, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:05:00');
INSERT INTO `operation_log` VALUES (36, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:05:13');
INSERT INTO `operation_log` VALUES (37, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:05:51');
INSERT INTO `operation_log` VALUES (38, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:05:53');
INSERT INTO `operation_log` VALUES (39, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:06:05');
INSERT INTO `operation_log` VALUES (40, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:08:54');
INSERT INTO `operation_log` VALUES (41, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:16:12');
INSERT INTO `operation_log` VALUES (42, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-09 21:31:33');
INSERT INTO `operation_log` VALUES (43, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-10 10:02:53');
INSERT INTO `operation_log` VALUES (44, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-10 10:06:24');
INSERT INTO `operation_log` VALUES (45, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-10 10:55:52');
INSERT INTO `operation_log` VALUES (46, 4, 'monitor2', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-10 10:57:18');
INSERT INTO `operation_log` VALUES (47, 4, 'monitor2', '用户登录', '手机验证码登录成功', 'success', '127.0.0.1', '2026-05-10 11:10:56');
INSERT INTO `operation_log` VALUES (48, 4, 'monitor2', '用户登录', '手机号+图形验证码登录成功', 'success', '127.0.0.1', '2026-05-10 12:00:33');
INSERT INTO `operation_log` VALUES (49, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-10 12:04:28');
INSERT INTO `operation_log` VALUES (50, 3, 'monitor1', '用户登录', '手机号+图形验证码登录成功', 'success', '127.0.0.1', '2026-05-10 12:06:28');
INSERT INTO `operation_log` VALUES (51, 4, 'monitor2', '用户登录', '手机号+图形验证码登录成功', 'success', '127.0.0.1', '2026-05-10 12:07:35');
INSERT INTO `operation_log` VALUES (52, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-10 12:08:01');
INSERT INTO `operation_log` VALUES (53, 4, 'monitor2', '用户登录', '手机号+图形验证码登录成功', 'success', '127.0.0.1', '2026-05-10 12:08:54');
INSERT INTO `operation_log` VALUES (54, 3, 'monitor1', '用户登录', '手机号+图形验证码登录成功', 'success', '127.0.0.1', '2026-05-10 12:29:49');
INSERT INTO `operation_log` VALUES (55, 9, 'researcher3', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-10 12:31:09');
INSERT INTO `operation_log` VALUES (56, 21, 'manager6', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-10 12:31:39');
INSERT INTO `operation_log` VALUES (57, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 18:43:28');
INSERT INTO `operation_log` VALUES (58, 3, 'monitor1', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 18:49:16');
INSERT INTO `operation_log` VALUES (59, 3, 'monitor1', '用户登录', '手机号+图形验证码登录成功', 'success', '127.0.0.1', '2026-05-12 18:49:51');
INSERT INTO `operation_log` VALUES (60, 3, 'monitor1', '用户登录', '手机号+图形验证码登录成功', 'success', '127.0.0.1', '2026-05-12 20:23:40');
INSERT INTO `operation_log` VALUES (61, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:26:34');
INSERT INTO `operation_log` VALUES (62, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:30:11');
INSERT INTO `operation_log` VALUES (63, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:31:05');
INSERT INTO `operation_log` VALUES (64, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:40:01');
INSERT INTO `operation_log` VALUES (65, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:42:49');
INSERT INTO `operation_log` VALUES (66, 3, 'monitor1', '用户登录', '手机号+图形验证码登录成功', 'success', '127.0.0.1', '2026-05-12 20:43:14');
INSERT INTO `operation_log` VALUES (67, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:45:24');
INSERT INTO `operation_log` VALUES (68, 7, 'researcher1', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:54:34');
INSERT INTO `operation_log` VALUES (69, 20, 'manager5', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:55:08');
INSERT INTO `operation_log` VALUES (70, 1, 'admin', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:55:42');
INSERT INTO `operation_log` VALUES (71, 3, 'monitor1', '用户登录', '手机号+图形验证码登录成功', 'success', '127.0.0.1', '2026-05-12 20:58:22');
INSERT INTO `operation_log` VALUES (72, 7, 'researcher1', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:58:50');
INSERT INTO `operation_log` VALUES (73, 11, 'manager1', '用户登录', '登录成功', 'success', '127.0.0.1', '2026-05-12 20:59:14');

-- ----------------------------
-- Table structure for processed_data
-- ----------------------------
DROP TABLE IF EXISTS `processed_data`;
CREATE TABLE `processed_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `source_id` bigint NOT NULL COMMENT '原始数据ID',
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `zone_id` bigint NULL DEFAULT NULL COMMENT '分区ID',
  `data_source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数据来源',
  `temperature` decimal(5, 2) NULL DEFAULT NULL COMMENT '温度(℃)',
  `humidity` decimal(5, 2) NULL DEFAULT NULL COMMENT '湿度(%)',
  `pm25` decimal(5, 2) NULL DEFAULT NULL COMMENT 'PM2.5(μg/m³)',
  `is_anomaly` tinyint NULL DEFAULT 0 COMMENT '是否异常：0-正常，1-异常',
  `anomaly_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '异常原因',
  `process_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '处理状态：pending-待处理，processed-已处理，failed-失败',
  `version` int NULL DEFAULT 1 COMMENT '数据版本',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_source_id`(`source_id` ASC) USING BTREE,
  INDEX `idx_area_zone`(`area_id` ASC, `zone_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '处理后数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of processed_data
-- ----------------------------
INSERT INTO `processed_data` VALUES (1, 1, 1, 1, 'sensor', 22.50, 55.00, 35.00, 0, NULL, 'processed', 1, '2026-05-09 14:53:12', '2026-05-09 15:03:12', '2026-05-09 15:03:12');
INSERT INTO `processed_data` VALUES (2, 2, 2, 3, 'sensor', 25.00, 60.00, 45.00, 0, NULL, 'processed', 1, '2026-05-09 14:43:12', '2026-05-09 15:03:12', '2026-05-09 15:03:12');
INSERT INTO `processed_data` VALUES (3, 4, 4, NULL, 'sensor', 32.00, 80.00, 120.00, 1, '温度超出正常范围', 'processed', 1, '2026-05-09 14:33:13', '2026-05-09 15:03:13', '2026-05-09 15:03:13');

-- ----------------------------
-- Table structure for sensor_data
-- ----------------------------
DROP TABLE IF EXISTS `sensor_data`;
CREATE TABLE `sensor_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `data_source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数据来源：sensor-传感器，drone-无人机，manual-人工录入，api-接口对接，import-历史导入',
  `temperature` decimal(5, 2) NULL DEFAULT NULL COMMENT '温度(℃)',
  `humidity` decimal(5, 2) NULL DEFAULT NULL COMMENT '湿度(%)',
  `precipitation` decimal(5, 2) NULL DEFAULT NULL COMMENT '降水量(mm)',
  `wind_speed` decimal(5, 2) NULL DEFAULT NULL COMMENT '风速(m/s)',
  `wind_direction` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风向',
  `light_intensity` decimal(8, 2) NULL DEFAULT NULL COMMENT '光照强度(lux)',
  `pm25` decimal(5, 2) NULL DEFAULT NULL COMMENT 'PM2.5(μg/m³)',
  `so2` decimal(5, 2) NULL DEFAULT NULL COMMENT 'SO₂(μg/m³)',
  `no2` decimal(5, 2) NULL DEFAULT NULL COMMENT 'NO₂(μg/m³)',
  `soil_moisture` decimal(5, 2) NULL DEFAULT NULL COMMENT '土壤墒情(%)',
  `soil_ph` decimal(4, 2) NULL DEFAULT NULL COMMENT '土壤pH值',
  `water_quality` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '水质等级',
  `crack_width` decimal(5, 2) NULL DEFAULT NULL COMMENT '裂缝宽度(mm)',
  `weathering_degree` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风化程度',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_area_time`(`area_id` ASC, `record_time` ASC) USING BTREE,
  INDEX `idx_source`(`data_source` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '多源环境数据采集表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sensor_data
-- ----------------------------
INSERT INTO `sensor_data` VALUES (1, 1, 'sensor', 22.50, 55.00, 0.00, 2.50, '东北', 45000.00, 35.00, 8.00, 25.00, 45.00, 6.80, '优', NULL, NULL, '2026-05-09 14:53:12', '2026-05-09 15:03:12');
INSERT INTO `sensor_data` VALUES (2, 2, 'sensor', 25.00, 60.00, 2.50, 3.20, '西北', 52000.00, 45.00, 12.00, 30.00, 50.00, 7.20, '良', NULL, NULL, '2026-05-09 14:43:12', '2026-05-09 15:03:12');
INSERT INTO `sensor_data` VALUES (3, 3, 'manual', 28.00, 70.00, NULL, NULL, NULL, NULL, 75.00, NULL, NULL, NULL, NULL, NULL, 0.50, '轻微', '2026-05-09 13:03:12', '2026-05-09 15:03:12');
INSERT INTO `sensor_data` VALUES (4, 4, 'sensor', 32.00, 80.00, 5.00, 4.50, '南风', 38000.00, 120.00, 25.00, 45.00, NULL, NULL, '中', NULL, NULL, '2026-05-09 14:33:12', '2026-05-09 15:03:12');
INSERT INTO `sensor_data` VALUES (5, 5, 'drone', 24.00, 58.00, NULL, NULL, NULL, 65000.00, 40.00, NULL, NULL, NULL, NULL, NULL, 1.20, '中度', '2026-05-08 15:03:12', '2026-05-09 15:03:12');
INSERT INTO `sensor_data` VALUES (6, 6, 'api', 23.00, 56.00, 1.00, 2.80, '东风', 48000.00, 38.00, 10.00, 28.00, 48.00, 7.00, '优', NULL, NULL, '2026-05-09 14:23:12', '2026-05-09 15:03:12');
INSERT INTO `sensor_data` VALUES (7, 1, 'import', 21.00, 52.00, 0.50, 2.00, '北风', 42000.00, 32.00, 7.00, 22.00, 43.00, 6.90, '优', NULL, NULL, '2026-05-02 15:03:12', '2026-05-09 15:03:12');
INSERT INTO `sensor_data` VALUES (8, 2, 'sensor', 26.00, 62.00, 3.00, 3.50, '西风', 50000.00, 48.00, 13.00, 32.00, 52.00, 7.30, '良', NULL, NULL, '2026-05-09 14:13:12', '2026-05-09 15:03:12');
INSERT INTO `sensor_data` VALUES (9, 3, 'manual', 27.50, 68.00, NULL, NULL, NULL, NULL, 70.00, NULL, NULL, NULL, NULL, NULL, 0.80, '轻微', '2026-05-09 12:03:12', '2026-05-09 15:03:12');
INSERT INTO `sensor_data` VALUES (10, 5, 'sensor', 23.50, 57.00, 0.00, 2.20, '东南', 63000.00, 42.00, 9.00, 26.00, 46.00, 7.10, '优', NULL, NULL, '2026-05-09 14:03:12', '2026-05-09 15:03:12');

-- ----------------------------
-- Table structure for sms_code
-- ----------------------------
DROP TABLE IF EXISTS `sms_code`;
CREATE TABLE `sms_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '短信验证码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_code
-- ----------------------------
INSERT INTO `sms_code` VALUES (1, '13800000004', '7174', '2026-05-10 11:15:36', '2026-05-10 11:10:35');

-- ----------------------------
-- Table structure for threshold_template
-- ----------------------------
DROP TABLE IF EXISTS `threshold_template`;
CREATE TABLE `threshold_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `heritage_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '遗产类型：ancient_building-古建筑，site-遗址，cave-石窟寺',
  `factor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '因子类型',
  `threshold_min` decimal(10, 2) NULL DEFAULT NULL COMMENT '最小阈值',
  `threshold_max` decimal(10, 2) NULL DEFAULT NULL COMMENT '最大阈值',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '说明',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '阈值模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of threshold_template
-- ----------------------------
INSERT INTO `threshold_template` VALUES (1, '木质古建筑', 'ancient_building', 'temperature', 10.00, 28.00, '木质古建筑温度标准', '2026-05-09 15:03:13');
INSERT INTO `threshold_template` VALUES (2, '木质古建筑', 'ancient_building', 'humidity', 40.00, 65.00, '木质古建筑怕潮湿', '2026-05-09 15:03:13');
INSERT INTO `threshold_template` VALUES (3, '石窟寺', 'cave', 'temperature', 5.00, 25.00, '石窟寺温度标准', '2026-05-09 15:03:13');
INSERT INTO `threshold_template` VALUES (4, '石窟寺', 'cave', 'humidity', 50.00, 70.00, '石窟寺湿度标准', '2026-05-09 15:03:13');
INSERT INTO `threshold_template` VALUES (5, '石窟寺', 'cave', 'so2', NULL, 40.00, '石窟寺怕酸雨', '2026-05-09 15:03:13');
INSERT INTO `threshold_template` VALUES (6, '遗址', 'site', 'temperature', 0.00, 35.00, '遗址温度标准', '2026-05-09 15:03:13');
INSERT INTO `threshold_template` VALUES (7, '遗址', 'site', 'humidity', 30.00, 80.00, '遗址湿度标准', '2026-05-09 15:03:13');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'monitor' COMMENT '角色：admin-系统管理员，monitor-监测人员，researcher-科研人员，manager-遗产地管理员',
  `enable` int NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `login_fail_count` int NULL DEFAULT 0 COMMENT '登录失败次数',
  `lock_time` datetime NULL DEFAULT NULL COMMENT '账号锁定时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'Admin@123', 'admin@heritage.com', '13800000001', '张伟', 'admin', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (2, 'admin2', 'Admin@123', 'admin2@heritage.com', '13800000002', '李娜', 'admin', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (3, 'monitor1', 'Monitor@123', 'monitor1@heritage.com', '13800000003', '王强', 'monitor', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (4, 'monitor2', 'Monitor@123', 'monitor2@heritage.com', '13800000004', '刘芳', 'monitor', 1, 2, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (5, 'monitor3', 'Monitor@123', 'monitor3@heritage.com', '13800000005', '陈明', 'monitor', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (6, 'monitor4', 'Monitor@123', 'monitor4@heritage.com', '13800000006', '赵丽', 'monitor', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (7, 'researcher1', 'Research@123', 'researcher1@heritage.com', '13800000007', '孙杰', 'researcher', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (8, 'researcher2', 'Research@123', 'researcher2@heritage.com', '13800000008', '周敏', 'researcher', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (9, 'researcher3', 'Research@123', 'researcher3@heritage.com', '13800000009', '吴涛', 'researcher', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (10, 'researcher4', 'Research@123', 'researcher4@heritage.com', '13800000010', '郑红', 'researcher', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (11, 'manager1', 'Manager@123', 'manager1@heritage.com', '13800000011', '钱勇', 'manager', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (12, 'manager2', 'Manager@123', 'manager2@heritage.com', '13800000012', '孙静', 'manager', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (13, 'manager3', 'Manager@123', 'manager3@heritage.com', '13800000013', '朱峰', 'manager', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (14, 'manager4', 'Manager@123', 'manager4@heritage.com', '13800000014', '许梅', 'manager', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (15, 'disabled_user', 'Disabled@123', 'disabled@heritage.com', '13800000015', '禁用用户', 'monitor', 0, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (16, 'monitor5', 'Monitor@123', 'monitor5@heritage.com', '13800000016', '黄勇', 'monitor', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (17, 'monitor6', 'Monitor@123', 'monitor6@heritage.com', '13800000017', '林静', 'monitor', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (18, 'researcher5', 'Research@123', 'researcher5@heritage.com', '13800000018', '何军', 'researcher', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (19, 'researcher6', 'Research@123', 'researcher6@heritage.com', '13800000019', '谢丽', 'researcher', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (20, 'manager5', 'Manager@123', 'manager5@heritage.com', '13800000020', '邓强', 'manager', 1, 0, NULL, '2026-05-09 15:03:12');
INSERT INTO `user` VALUES (21, 'manager6', 'Manager@123', 'manager6@heritage.com', '13800000021', '曾敏', 'manager', 1, 0, NULL, '2026-05-09 15:03:12');

-- ----------------------------
-- Table structure for user_area_permission
-- ----------------------------
DROP TABLE IF EXISTS `user_area_permission`;
CREATE TABLE `user_area_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `area_id` bigint NOT NULL COMMENT '区域ID',
  `permission_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限类型：operate-操作权限，analyze-分析权限，view-查看权限',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户区域权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_area_permission
-- ----------------------------
INSERT INTO `user_area_permission` VALUES (7, 16, 1, 'operate', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (8, 17, 2, 'operate', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (9, 7, 1, 'analyze', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (10, 7, 2, 'analyze', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (11, 7, 3, 'analyze', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (12, 8, 4, 'analyze', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (13, 8, 5, 'analyze', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (14, 9, 6, 'analyze', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (15, 10, 1, 'analyze', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (16, 18, 2, 'analyze', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (17, 19, 3, 'analyze', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (18, 11, 1, 'view', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (19, 11, 2, 'view', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (20, 12, 3, 'view', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (21, 12, 4, 'view', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (22, 13, 5, 'view', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (23, 14, 6, 'view', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (24, 20, 1, 'view', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (25, 21, 2, 'view', '2026-05-09 15:03:12');
INSERT INTO `user_area_permission` VALUES (32, 4, 3, 'view', '2026-05-10 12:08:16');
INSERT INTO `user_area_permission` VALUES (33, 4, 4, 'view', '2026-05-10 12:08:16');
INSERT INTO `user_area_permission` VALUES (34, 3, 1, 'view', '2026-05-10 12:08:20');
INSERT INTO `user_area_permission` VALUES (35, 3, 2, 'view', '2026-05-10 12:08:20');
INSERT INTO `user_area_permission` VALUES (36, 5, 5, 'view', '2026-05-10 12:08:24');
INSERT INTO `user_area_permission` VALUES (37, 6, 6, 'view', '2026-05-10 12:08:28');

SET FOREIGN_KEY_CHECKS = 1;
