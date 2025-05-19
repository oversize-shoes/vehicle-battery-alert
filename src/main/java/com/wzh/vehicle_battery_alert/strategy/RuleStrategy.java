package com.wzh.vehicle_battery_alert.strategy;

import com.wzh.vehicle_battery_alert.model.Rule;
import com.wzh.vehicle_battery_alert.vo.WarnVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface RuleStrategy {
    WarnVO ruleAlert(String batteryType, Map<String, BigDecimal> data, Rule rules);
}
