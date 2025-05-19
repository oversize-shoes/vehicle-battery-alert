package com.wzh.vehicle_battery_alert.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@Component
public class IdGenerator {
    private final Snowflake snowflake = IdUtil.getSnowflake(1,1);

    public long nextId(){
        return snowflake.nextId();
    }
}
