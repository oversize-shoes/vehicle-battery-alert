package com.wzh.vehicle_battery_alert.service;

import com.wzh.vehicle_battery_alert.common.IdempotentException;
import com.wzh.vehicle_battery_alert.mapper.BatteryAlertMapper;
import com.wzh.vehicle_battery_alert.model.BatteryAlert;
import com.wzh.vehicle_battery_alert.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@Service
public class BatteryAlertService {

    @Autowired
    private BatteryAlertMapper batteryAlertMapper;

    @Autowired
    private RedisTemplate<String ,Object> redisTemplate;


    public void insert(BatteryAlert alert) {

        // 构造幂等 Redis key
        String redisKey = RedisKeyUtil.buildAlertIdempotentKey(alert.getVin(), alert.getRuleId());

        // 幂等校验：如果 key 已存在，说明已经处理过这个预警了，直接返回
        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, 1, 10, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(success)) {
            throw new IdempotentException("重复请求，已处理");
        }
        batteryAlertMapper.insert(alert);
    }
}
