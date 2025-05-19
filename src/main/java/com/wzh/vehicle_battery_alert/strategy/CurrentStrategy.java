package com.wzh.vehicle_battery_alert.strategy;

import com.wzh.vehicle_battery_alert.model.Rule;
import com.wzh.vehicle_battery_alert.vo.WarnVO;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author WZH
 * @date 2025/5/18
 **/

public class CurrentStrategy implements RuleStrategy {

    @Override
    public WarnVO ruleAlert(String batteryType, Map<String, BigDecimal> data, Rule rule) {
        BigDecimal maxVoltage = data.getOrDefault("Ix", BigDecimal.ZERO);
        BigDecimal minVoltage = data.getOrDefault("Ii", BigDecimal.ZERO);
        BigDecimal diff = maxVoltage.subtract(minVoltage);

        if (diff.compareTo(rule.getMinValue()) >= 0 && diff.compareTo(rule.getMaxValue()) <= 0) {
            return new WarnVO(null, batteryType, rule.getRuleName(), rule.getLevel());
        }
        return null;
    }
}
