package com.wzh.vehicle_battery_alert.dto;

import lombok.Data;

import javax.annotation.security.DenyAll;
import java.io.Serializable;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@Data
public class WarnDTO implements Serializable {
    private Long carId;
    private Long warnId;
    private String signal;
}
