package com.wzh.vehicle_battery_alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.Serializable;

/**
 * @author WZH
 * @date 2025/5/19
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle implements Serializable {
    private String vid;
    private Long vin;
    private String batteryType;
    private Integer mileage;
    private Integer health;
}
