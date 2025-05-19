package com.wzh.vehicle_battery_alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule {
    private Long id;
    private Integer ruleId;
    private String ruleName;
    private String batteryType;
    private Integer level;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private String unit;
    private Integer status;
    private LocalDateTime createTime;
}
