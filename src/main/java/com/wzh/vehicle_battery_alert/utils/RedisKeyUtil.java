package com.wzh.vehicle_battery_alert.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author WZH
 * @date 2025/5/18
 **/

public class RedisKeyUtil {
    public static String buildSignalKey(Long vin, String signalType, LocalDateTime time) {
        return String.format("battery:signal:%d:%s:%s",
                vin, signalType, time.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
    }

    public static String buildAlertIdempotentKey(Long vin, Long ruleId) {
        return String.format("battery:alert:%d:rule_%d", vin, ruleId);
    }

    public static String buildRuleCacheKey(String batteryType) {
        return String.format("battery:rule:%s", batteryType);
    }

    public static String buildVehicleInfoKey(Long vin) {
        return String.format("battery:vehicle:%d", vin);
    }

    public static String buildSignalCacheKeyById(Long id) {
        return String.format("battery:signal:id:%d", id);
    }

    public static String buildRuleCacheKey(String batteryType, Long ruleId) {
        return String.format("battery:rule:%s:ruleId_%d", batteryType, ruleId);
    }

    public static String buildAllListKey(Long vin){
        return "battery:signal:all:" + vin;
    }


}
