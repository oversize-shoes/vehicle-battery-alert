package com.wzh.vehicle_battery_alert.common;

/**
 * @author WZH
 * @date 2025/5/19
 **/
//校验参数异常
public class ParamsException extends RuntimeException {
    public ParamsException(String message){
        super(message);
    }
}
