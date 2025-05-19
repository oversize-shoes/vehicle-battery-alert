package com.wzh.vehicle_battery_alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatteryAlert {
    private Long id;
    private Long vin;
    private Long ruleId;
    private Integer alertLevel;
    private LocalDateTime alertTime;
    private Integer status;
    private String message;
    private LocalDateTime createdAt;
}
