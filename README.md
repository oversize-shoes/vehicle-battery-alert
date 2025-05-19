# 小米汽车电池预警

## 一、系统设计

### 1.项目简介

​	本系统为“小米汽车电池信号预警系统”，用于采集车辆电池信号数据，并根据预警规则进行风险识别、报警生成与查询。系统支持规则动态配置、分库分表管理信号与报警数据、缓存加速信号查询、并通过 RocketMQ 实现预警异步处理。



### 2.模块功能说明

#### 2.1 电池信号模块（BatterySignal）

- 接收信号上报（新增、修改、删除、查询）
- 每月按月分表，如 `battery_signal_202505_0` ~ `3`
- 使用 Redis 缓存热点信号，保证查询一致性

#### 2.2 车辆信息模块（Vehicle）

- 存储车辆基本信息（VIN、电池类型）
- 使用 ShardingSphere 分表（如 `vehicle_0` ~ `vehicle_7`）

#### 2.3 预警规则模块（AlertRule）

- 存储电池预警规则（按 battery_type + rule_id 区分）
- 动态维护规则阈值、等级、单位等

#### 2.4 预警生成模块（Warn）

- 支持定时任务批量触发、也支持外部接口触发（POST `/api/warn`）
- 匹配规则后生成对应预警，写入 `battery_alert_202505_x` 表
- 通过 RocketMQ 异步下发处理任务



### 3.项目亮点

#### 3.1 动态规则解析

- 规则数据存储在 `alert_rule` 表中，而非写死在代码中。
- 预警模块根据 `rule_id` + `battery_type` 动态查询规则，调用策略工厂匹配具体策略类进行判断。
- 实现了良好的扩展性，例如增加新规则时只需新增数据库数据和策略实现类，无需改动主流程代码。

#### 3.2 实时预警判断，性能优化。

- 使用 Redis 读取缓存的信号数据（热点 VIN），避免频繁数据库查询。
- 使用 RocketMQ 实现异步处理，避免阻塞主线程。
- 提供了 `/api/warn` 接口支持批量传入信号，减少请求次数，提升吞吐量。
- 可以通过 JMH 或 Spring Boot Actuator 进一步测试 P99 时间。

#### 3.3 面向海量数据的分库分表设计

- 信号表 `battery_signal_YYYYMM_i` 和预警表 `battery_alert_YYYYMM_i` 均按月 + 哈希分表，当前已建 `202505` 表。
- 使用 ShardingSphere 实现按 `vin` 分片（INLINE 算法），保证写入/查询负载均衡。
- 表结构简单且按时间 + vin 分片，便于后期扩展为冷热数据归档方案。

#### 数据一致性设计说明

#### 为了保证数据一致性，系统采用以下机制：

1. **幂等性控制**：
   - 通过 Redis 分布式锁控制短时间内相同 VIN 的重复写入，避免重复插入信号或预警记录。
   - `BatterySignal` 表中设置 `version` 字段用于乐观锁控制。
2. **缓存一致性维护**：
   - 对 VIN 信号使用 Redis 缓存，写入数据库后同步更新缓存。
   - 所有更新信号的操作均同步更新 Redis，避免缓存数据陈旧。
3. **消息异步可靠性**：
   - 采用 RocketMQ 异步处理信号预警，确保写入与告警生成解耦。
   - 消费端记录消费状态，避免重复生成预警。



## 二、接口文档

### 电池信号接口（BatterySignalController）

基础路径：`/api/signal`

| 方法   | 路径                   | 描述                    | 请求体类型         | 返回示例                         |
| ------ | ---------------------- | ----------------------- | ------------------ | -------------------------------- |
| POST   | `/api/signal`          | 新增电池信号            | BatterySignal 对象 | `{ code: 200, msg: "添加成功" }` |
| GET    | `/api/signal/{id}`     | 根据 VIN 查询最新信号   | 无                 | BatterySignal 对象数据           |
| GET    | `/api/signal/all/{id}` | 查询某 VIN 所有信号历史 | 无                 | BatterySignal 列表数据           |
| DELETE | `/api/signal/{id}`     | 删除某 VIN 最新信号记录 | 无                 | `{ code: 200, msg: "删除成功" }` |
| PUT    | `/api/signal`          | 更新电池信号记录        | BatterySignal 对象 | `{ code: 200, msg: "更新成功" }` |



> ```
> BatterySignal` 示例字段：`id`, `vin`, `signalType`, `signalValue`, `reportTime`, `createdAt`, `version
> ```

------

### 电池预警接口（WarnController）

基础路径：`/api/warn`

| 方法 | 路径        | 描述                   | 请求体类型      | 返回类型                |
| ---- | ----------- | ---------------------- | --------------- | ----------------------- |
| POST | `/api/warn` | 对信号列表进行预警判断 | `List<WarnDTO>` | `List<WarnVO>` 预警结果 |

Body：格式为数组，数组内的每个元素包含以下字段.

| 字段   | 类型   | 是否必传 | 含义     | 示例                | 备注                   |
| ------ | ------ | -------- | -------- | ------------------- | ---------------------- |
| carId  | int    | 必须     | 车架编号 | 1                   |                        |
| warnId | int    | 非必须   | 规则编号 | 1                   | 不传时候，遍历所有规则 |
| signal | String | 必须     | 信号     | {"Mx":1.0,"Mi":0.6} |                        |

Body示例：

```json
[
  {
    "carId": 1,
    "warnId": 1,
    "signal": "{\"Mx\":12.0,\"Mi\":0.6}"
  },
  {
    "carId": 2,
    "warnId": 2,
    "signal": "{\"Ix\":12.0,\"Ii\":11.7}"
  }，
   {
    "carId": 3,
    "signal": "{\"Mx\":11.0,\"Mi\":9.6,\"Ix\":12.0,\"Ii\":11.7}"
  }
]
```

返回示例：

```json
{
    "status": 200,
    "msg": "ok",
    "data": 
        [
            {
                "车架编号": 1,
                "电池类型": "三元电池",
                "warnName": "电压差报警",
                "warnLevel": 0
            },
            {
                "车架编号": 2,
                "电池类型": "铁锂电池",
                "warnName": "电流差报警",
                "warnLevel": 2
            },
            {
                "车架编号": 3,
                "电池类型": "三元电池",
                "warnName": "电压差报警",
                "warnLevel": 2
            },
            {
                "车架编号": 3,
                "电池类型": "三元电池",
                "warnName": "电流差报警",
                "warnLevel": 2
            }
         ]
}
```



## 三、建表SQL语句

### 规则表：alert_rule

```sql
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

```

### 警告表：  battery_alert_202505_x

```sql
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
```

说明：`battery_alert_202505_0 ~ `_3` 表结构相同，仅为分表

### 信号表：battery_signal_202505_x

```sql
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
```

说明：`battery_signal_202505_0 ~ `_3` 表结构相同，仅为分表

### 汽车表：vehicle_x

```sql
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

```

说明：`vehicle_0 ~ `vehicle_3` 表结构相同，仅为分表



