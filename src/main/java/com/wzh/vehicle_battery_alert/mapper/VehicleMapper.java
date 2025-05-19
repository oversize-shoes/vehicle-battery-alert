package com.wzh.vehicle_battery_alert.mapper;

import com.wzh.vehicle_battery_alert.model.Vehicle;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VehicleMapper {

    @Select("SELECT battery_type FROM vehicle WHERE vin = #{carId}")
    String getBatteryTypeByCarId(Long vin);

    @Insert("INSERT INTO vehicle (vid ,vin, battery_type, mileage, health) VALUES (#{vid},#{vin}, #{batteryType}, #{mileage}, #{health})")
    void insert(Vehicle vehicle);
}
