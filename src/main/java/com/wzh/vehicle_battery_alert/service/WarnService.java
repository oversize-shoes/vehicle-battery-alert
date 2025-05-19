package com.wzh.vehicle_battery_alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzh.vehicle_battery_alert.common.ParamsException;
import com.wzh.vehicle_battery_alert.dto.WarnDTO;
import com.wzh.vehicle_battery_alert.mapper.RuleMapper;
import com.wzh.vehicle_battery_alert.mapper.VehicleMapper;
import com.wzh.vehicle_battery_alert.model.BatteryAlert;
import com.wzh.vehicle_battery_alert.model.BatterySignal;
import com.wzh.vehicle_battery_alert.model.Rule;
import com.wzh.vehicle_battery_alert.strategy.RuleStrategy;
import com.wzh.vehicle_battery_alert.strategy.RuleStrategyFactory;
import com.wzh.vehicle_battery_alert.utils.IdGenerator;
import com.wzh.vehicle_battery_alert.utils.RedisKeyUtil;
import com.wzh.vehicle_battery_alert.utils.WarnDTOValidator;
import com.wzh.vehicle_battery_alert.vo.WarnVO;
import org.apache.commons.digester.plugins.RulesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@Service
public class WarnService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private VehicleMapper vehicleMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private BatteryAlertService batteryAlertService;
    @Autowired
    private BatterySignalService batterySignalService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //解析json数据
    private Map<String, BigDecimal> parseJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, BigDecimal>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("解析 signal JSON 出错", e);
        }
    }


    public List<WarnVO> warnEvaluate(List<WarnDTO> request)  {

        Set<WarnVO> result = new LinkedHashSet<>();

        for (WarnDTO item : request) {
            //参数校验
            WarnDTOValidator.validate(item);

            //查询车辆电池信息
            Long carId = item.getCarId();
            String signalJson = item.getSignal();
            //先查redis
            String BatteryTypeKey = RedisKeyUtil.buildVehicleInfoKey(carId);
            String BatteryType = (String) redisTemplate.opsForValue().get(BatteryTypeKey);
            if(BatteryType == null){
                BatteryType = vehicleMapper.getBatteryTypeByCarId(carId);
                if (BatteryType != null) {
                    redisTemplate.opsForValue().set(BatteryTypeKey, BatteryType, 30, TimeUnit.MINUTES);
                }
            }

            //查询电池规则
            List<Rule> rules ;

            if (item.getWarnId() == null) {//没有传规则id
                String ruleKey = RedisKeyUtil.buildRuleCacheKey(BatteryType);
                rules = (List<Rule>) redisTemplate.opsForValue().get(ruleKey);
                if(rules == null){
                    rules = ruleMapper.selectAllByBatteryType(BatteryType);
                    if (rules != null && !rules.isEmpty()) {
                        redisTemplate.opsForValue().set(ruleKey, rules, 30, TimeUnit.MINUTES);
                    }
                }
            } else {
                String ruleKey = RedisKeyUtil.buildRuleCacheKey(BatteryType,item.getWarnId());
                rules = (List<Rule>) redisTemplate.opsForValue().get(ruleKey);
                if (rules == null) {
                    rules = ruleMapper.selectByBatteryTypeAndRuleId(BatteryType, item.getWarnId());
                    if (rules != null && !rules.isEmpty()) {
                        redisTemplate.opsForValue().set(ruleKey, rules, 10, TimeUnit.MINUTES);
                    }
                }
            }

            //解析json数据
            Map<String, BigDecimal> data = this.parseJson(item.getSignal());

            String batchUuid = UUID.randomUUID().toString();
            //根据信号判断电池预警等级
            List<String> triggeredSignalTypes = new ArrayList<>();
            for (Rule rule : rules) {
                RuleStrategy strategy = RuleStrategyFactory.getStrategy(rule.getRuleId());
                if (strategy == null) continue;


                WarnVO vo = strategy.ruleAlert(BatteryType, data, rule);
                if (vo == null) continue;

                // 仅传当前 rule，而不是所有 rules
                vo = strategy.ruleAlert(BatteryType, data, rule);
                if (vo != null) {
                    vo.setCarId(carId);
                    result.add(vo);
                    triggeredSignalTypes.add(rule.getRuleName());

                    BatteryAlert alert = new BatteryAlert();
                    alert.setId(idGenerator.nextId());
                    alert.setVin(carId);
                    alert.setRuleId(rule.getRuleId().longValue());
                    alert.setAlertLevel(rule.getLevel());
                    alert.setAlertTime(LocalDateTime.now());
                    alert.setCreatedAt(LocalDateTime.now());
                    alert.setStatus(0);
                    alert.setMessage("命中规则：" + rule.getRuleName());

                    //插入BatterySignal
                    BatterySignal signal = new BatterySignal();
                    signal.setId(idGenerator.nextId());
                    signal.setVin(carId);
                    signal.setSignalType(rule.getRuleName());
                    signal.setSignalValue(signalJson);
                    signal.setReportTime(LocalDateTime.now());
                    signal.setCreatedAt(LocalDateTime.now());
                    signal.setVersion(0);
                    signal.setUuid(batchUuid);


                    batteryAlertService.insert(alert);

                }
                result.add(vo);
                if(!triggeredSignalTypes.isEmpty()){
                    String mergedSignalType = String.join("且", triggeredSignalTypes);

                    BatterySignal signal = new BatterySignal();
                    signal.setId(idGenerator.nextId());
                    signal.setVin(carId);
                    signal.setSignalType(mergedSignalType); // "电压差报警且电流差报警"
                    signal.setSignalValue(signalJson);
                    signal.setReportTime(LocalDateTime.now());
                    signal.setCreatedAt(LocalDateTime.now());
                    signal.setVersion(0);
                    signal.setUuid(batchUuid);

                    batterySignalService.insert(signal);
                }
            }
        }
        return new ArrayList<>(result);
    }



}

