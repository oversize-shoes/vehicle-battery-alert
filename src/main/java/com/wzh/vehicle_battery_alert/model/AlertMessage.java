package com.wzh.vehicle_battery_alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author WZH
 * @date 2025/5/19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertMessage implements Serializable {
    private Long alertId;
    private String tableName;
    private Long vin;
    private Integer alertLevel;
    private String ruleName;
    private String message;
}
