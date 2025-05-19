package com.wzh.vehicle_battery_alert.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WZH
 * @date 2025/5/18
 **/

public class RuleStrategyFactory {
    private static final Map<Integer,RuleStrategy> STRATEGY_MAP= new HashMap<>();

    static {
        STRATEGY_MAP.put(1, new VoltageStrategy());
        STRATEGY_MAP.put(2, new CurrentStrategy());
    }

    public static RuleStrategy getStrategy(Integer ruleId) {
        return STRATEGY_MAP.get(ruleId);
    }
}
