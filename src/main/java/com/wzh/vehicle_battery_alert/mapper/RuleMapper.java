package com.wzh.vehicle_battery_alert.mapper;

import com.wzh.vehicle_battery_alert.model.Rule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RuleMapper {
    @Select("SELECT id, rule_id, rule_name, battery_type, level, min_value, max_value, unit, status, create_time " +
            "FROM alert_rule WHERE battery_type = #{batteryType} AND status = 1")
    List<Rule> selectAllByBatteryType(@Param("batteryType") String batteryType);

    @Select("SELECT id, rule_id, rule_name, battery_type, level, min_value, max_value, unit, status, create_time " +
            "FROM alert_rule WHERE battery_type = #{batteryType} AND rule_id = #{ruleId} AND status = 1")
    List<Rule> selectByBatteryTypeAndRuleId(@Param("batteryType") String batteryType,
                                            @Param("ruleId") Long ruleId);


}
