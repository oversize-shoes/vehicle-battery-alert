package com.wzh.vehicle_battery_alert.mapper;

import com.wzh.vehicle_battery_alert.model.BatteryAlert;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BatteryAlertMapper {
    @Insert("INSERT INTO battery_alert (id, vin, rule_id, alert_level, alert_time, status, message, created_at) " +
            "VALUES (#{id}, #{vin}, #{ruleId}, #{alertLevel}, #{alertTime}, #{status}, #{message}, #{createdAt})")
    void insert(BatteryAlert alert);


    @Update("UPDATE ${table} SET status = #{status} WHERE id = #{id}")
    void updateStatusById(@Param("table") String table, @Param("id") Long id, @Param("status") int status);

    @Select("SELECT * FROM ${table} WHERE status = 0")
    List<BatteryAlert> selectUnprocessedByTable(@Param("table") String table);
}
