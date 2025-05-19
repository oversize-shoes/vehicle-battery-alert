package com.wzh.vehicle_battery_alert.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzh.vehicle_battery_alert.dto.WarnDTO;

import java.util.*;

/**
 * @author WZH
 * @date 2025/5/19
 **/

public class WarnDTOValidator {
    private static final Map<Long, Set<String>> ruleSignalMap = new HashMap<>();

    static {
        ruleSignalMap.put(1L, new HashSet<>(Arrays.asList("Mx", "Mi")));
        ruleSignalMap.put(2L, new HashSet<>(Arrays.asList("Ix", "Ii")));
    }

    public static void validate(WarnDTO dto) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Double> signalMap = mapper.readValue(dto.getSignal(), new TypeReference<Map<String, Double>>() {});

            // 判断是否规则与信号类型匹配
            Set<String> expectedKeys = ruleSignalMap.get(dto.getWarnId());
            if (expectedKeys != null && !signalMap.keySet().containsAll(expectedKeys)) {
                throw new IllegalArgumentException("规则与信号类型不匹配，应包含: " + expectedKeys);
            }

            // 判断是否值逻辑错误（如最小大于最大）
            if ((signalMap.containsKey("Mi") && signalMap.containsKey("Mx")) && signalMap.get("Mi") > signalMap.get("Mx")) {
                throw new IllegalArgumentException("电压差参数错误：Mi > Mx");
            }
            if ((signalMap.containsKey("Ii") && signalMap.containsKey("Ix")) && signalMap.get("Ii") > signalMap.get("Ix")) {
                throw new IllegalArgumentException("电流差参数错误：Ii > Ix");
            }
        } catch (Exception e) {
            throw new RuntimeException("参数解析或校验失败: " + e.getMessage(), e);
        }
    }
}
