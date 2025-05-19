package com.wzh.vehicle_battery_alert.mapper;

import com.wzh.vehicle_battery_alert.model.BatteryAlert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


import java.util.List;

@Mapper
public interface DynamicAlertMapper {
    @Update("UPDATE ${table} SET status = #{status} WHERE id = #{id}")
    void updateStatusById(@Param("table") String table, @Param("id") Long id, @Param("status") int status);

    @Select("SELECT * FROM ${table} WHERE status = 0")
    List<BatteryAlert> selectUnprocessedByTable(@Param("table") String table);
}
