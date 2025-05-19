package com.wzh.vehicle_battery_alert;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@EnableScheduling
@SpringBootApplication
public class VehicleBatteryAlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleBatteryAlertApplication.class, args);
    }

}
