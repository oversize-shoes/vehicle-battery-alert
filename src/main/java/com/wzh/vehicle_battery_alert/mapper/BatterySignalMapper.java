package com.wzh.vehicle_battery_alert.mapper;


import com.wzh.vehicle_battery_alert.model.BatterySignal;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BatterySignalMapper {

    @Insert("INSERT INTO battery_signal (id, vin, signal_type, signal_value, report_time, created_at, version, uuid) " +
            "VALUES (#{id}, #{vin}, #{signalType}, #{signalValue}, #{reportTime}, #{createdAt}, #{version}, #{uuid})")
    void insert(BatterySignal signal);

    @Select("SELECT id, vin, signal_type, signal_value, report_time, created_at, version, uuid FROM battery_signal WHERE vin = #{id} ORDER BY report_time DESC LIMIT 1")
    BatterySignal selectByVin(Long id);

    @Select("SELECT id, vin, signal_type, signal_value, report_time, created_at, version, uuid FROM battery_signal WHERE vin = #{vin}")
    List<BatterySignal> selectAll(Long vin);

    @Select("SELECT COUNT(*) FROM battery_signal WHERE uuid = #{uuid}")
    boolean existsByUuid(String uuid);

    @Delete("DELETE FROM battery_signal WHERE vin = #{id}")
    void deleteById(Long id);

    @Update("UPDATE battery_signal SET vin=#{vin}, signal_type=#{signalType}, signal_value=#{signalValue}, report_time=#{reportTime}, created_at=#{createdAt}, version = version + 1 WHERE vin = #{vin} AND version = #{version}")
    int updateWithVersion(BatterySignal signal);
}
