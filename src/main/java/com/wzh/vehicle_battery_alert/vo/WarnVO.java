package com.wzh.vehicle_battery_alert.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarnVO implements Serializable {
    private Long carId;
    private String batteryType;
    private String warnName;
    private Integer warnLevel;
}
