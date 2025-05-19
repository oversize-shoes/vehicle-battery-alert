package com.wzh.vehicle_battery_alert.mq;

import com.wzh.vehicle_battery_alert.mapper.BatteryAlertMapper;
import com.wzh.vehicle_battery_alert.mapper.DynamicAlertMapper;
import com.wzh.vehicle_battery_alert.model.AlertMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author WZH
 * @date 2025/5/19
 **/
@Slf4j
@Service
@RocketMQMessageListener(
        topic = "${mq.topic.alert-process}",
        consumerGroup = "battery-alert-consumer-group"
)
@RequiredArgsConstructor
public class AlertConsumer implements RocketMQListener<AlertMessage> {

    private final BatteryAlertMapper batteryAlertMapper;

    @Override
    public void onMessage(AlertMessage message) {
        log.info("接收到预警处理消息：alertId={}, vin={}", message.getAlertId(), message.getVin());
        batteryAlertMapper.updateStatusById(message.getTableName(), message.getAlertId(), 1);
        log.info("预警处理完成，status已更新为1");
    }
}
