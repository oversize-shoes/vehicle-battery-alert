package com.wzh.vehicle_battery_alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatterySignal implements Serializable {
    private Long id;
    private Long vin;
    private String signalType;
    private String signalValue;
    private LocalDateTime reportTime;
    private LocalDateTime createdAt;
    private Integer version;
    private String uuid;

}
