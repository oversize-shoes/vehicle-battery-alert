package com.wzh.vehicle_battery_alert.mq;

import com.wzh.vehicle_battery_alert.model.AlertMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author WZH
 * @date 2025/5/19
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertProducer {
    private final RocketMQTemplate rocketMQTemplate;

    @Value("${mq.topic.alert-process}")
    private String topic;

    public void send(AlertMessage message) {
        rocketMQTemplate.convertAndSend(topic, message);
        log.info("已发送预警处理MQ消息，alertId={}, table={}", message.getAlertId(), message.getTableName());
    }
}
