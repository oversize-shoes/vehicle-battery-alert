package com.wzh.vehicle_battery_alert.common;

/**
 * @author WZH
 * @date 2025/5/19
 **/
//幂等异常
public class IdempotentException extends RuntimeException {
    public IdempotentException(String message) {
        super(message);
    }
}
