package com.wzh.vehicle_battery_alert.job;


import com.wzh.vehicle_battery_alert.mapper.BatteryAlertMapper;
import com.wzh.vehicle_battery_alert.model.AlertMessage;
import com.wzh.vehicle_battery_alert.model.BatteryAlert;
import com.wzh.vehicle_battery_alert.mq.AlertProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author WZH
 * @date 2025/5/19
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertJob {

    private final AlertProducer alertProducer;
    private final BatteryAlertMapper batteryAlertMapper;

    @Scheduled(cron = "0 */1 * * * ?") // 每分钟执行一次
    public void scanUnprocessedAlerts() {
        String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        for (int i = 0; i < 4; i++) {
            String table = "battery_alert_" + yearMonth + "_" + i;
            try {
                log.info("正在扫描 {} 中的未处理预警", table);
                List<BatteryAlert> list = batteryAlertMapper.selectUnprocessedByTable(table);
                for (BatteryAlert alert : list) {
                    AlertMessage message = new AlertMessage();
                    message.setAlertId(alert.getId());
                    message.setTableName(table);
                    message.setVin(alert.getVin());
                    message.setAlertLevel(alert.getAlertLevel());
                    message.setRuleName("规则ID:" + alert.getRuleId());
                    message.setMessage(alert.getMessage());
                    alertProducer.send(message);
                }
            } catch (Exception e) {
                log.error("扫描表 {} 时出现异常: {}", table, e.getMessage(), e);
            }
        }
    }
}

