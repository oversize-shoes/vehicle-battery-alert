package com.wzh.vehicle_battery_alert.service;

import com.wzh.vehicle_battery_alert.common.IdempotentException;
import com.wzh.vehicle_battery_alert.mapper.BatterySignalMapper;
import com.wzh.vehicle_battery_alert.model.BatterySignal;
import com.wzh.vehicle_battery_alert.utils.IdGenerator;
import com.wzh.vehicle_battery_alert.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@Service
public class BatterySignalService {
    @Autowired
    private BatterySignalMapper batterySignalMapper;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String LOCK_PREFIX = "lock_signal:";

    public BatterySignal getByVin(Long vin) {
        String key = RedisKeyUtil.buildSignalCacheKeyById(vin);

        // 先查 Redis
        BatterySignal signal = (BatterySignal) redisTemplate.opsForValue().get(key);
        if (signal != null) {
            return signal;
        }

        // Redis 中没有，查数据库
        BatterySignal list = batterySignalMapper.selectByVin(vin); // limit 1 查询
        if (list != null ) {
            // 存入 Redis（可设置过期时间）
            redisTemplate.opsForValue().set(key, signal, 10, TimeUnit.MINUTES);
            return list;
        }
        return null;
    }


    public void insert(BatterySignal batterySignal){
        if (batterySignal.getUuid() == null) {
            batterySignal.setUuid(UUID.randomUUID().toString());
        }

        String key = RedisKeyUtil.buildSignalCacheKeyById(batterySignal.getVin());
        String allKey = RedisKeyUtil.buildAllListKey(batterySignal.getVin());
        String idpRaw = batterySignal.getVin() + batterySignal.getSignalValue().toString() + batterySignal.getSignalType();
        String idpKey = DigestUtils.md5DigestAsHex(idpRaw.getBytes());

        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent("idp:signal:" + idpKey, 1, 10, TimeUnit.SECONDS))) {
            throw new IdempotentException("重复请求，已处理");
        }

        batterySignal.setId(idGenerator.nextId());
        String lockKey = LOCK_PREFIX + idpKey;
        String lockValue = "thread:" + Thread.currentThread().getId();

        try {
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 5, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(locked)) {
                //第一次删缓存
                redisTemplate.delete(key);
                redisTemplate.delete(allKey);

                batterySignal.setVersion(0);
                batterySignalMapper.insert(batterySignal);


                // 延迟双删（异步删除一次）
                delayDelete(key);
                delayDelete(allKey);

            } else {
                throw new RuntimeException("写入冲突，稍后重试");
            }
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    //释放锁
    private void releaseLock(String key, String value) {
        String script = "if redis.call(\"get\", KEYS[1]) == ARGV[1] then return redis.call(\"del\", KEYS[1]) else return 0 end";
        redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(key), value);
    }

    public void update(BatterySignal signal) {
        String lockKey = LOCK_PREFIX + signal.getId();
        String lockValue = UUID.randomUUID().toString();

        String key = RedisKeyUtil.buildSignalCacheKeyById(signal.getVin());
        String allKey = RedisKeyUtil.buildAllListKey(signal.getVin());
        try {
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 5, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(locked)) {
                BatterySignal dbSignal = batterySignalMapper.selectByVin(signal.getVin());
                if (dbSignal == null) throw new RuntimeException("信号不存在");

                signal.setVersion(dbSignal.getVersion());
                redisTemplate.delete(key);
                redisTemplate.delete(allKey);

                int rows = batterySignalMapper.updateWithVersion(signal);
                if (rows == 0) throw new RuntimeException("数据已被修改，请刷新重试");

                //删除缓存 + 延迟双删
                delayDelete(key);
                delayDelete(allKey);

            } else {
                throw new RuntimeException("更新冲突，请稍后重试");
            }
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }


    public List<BatterySignal> listAll(Long id) {
        String key = RedisKeyUtil.buildAllListKey(id);
        List<BatterySignal> cachedList = (List<BatterySignal>) redisTemplate.opsForValue().get(key);
        if (cachedList != null && !cachedList.isEmpty()) {
            return cachedList;
        }

        List<BatterySignal> list = batterySignalMapper.selectAll(id);
        if (list != null && !list.isEmpty()) {
            redisTemplate.opsForValue().set(key, list, 10, TimeUnit.MINUTES);
        }
        return batterySignalMapper.selectAll(id);
    }

    public void delete(Long id) {
        String key = RedisKeyUtil.buildSignalCacheKeyById(id);
        String allKey = RedisKeyUtil.buildAllListKey(id);

        // 1. 删除 Redis 缓存
        redisTemplate.delete(key);
        redisTemplate.delete(allKey);

        // 2. 删除数据库
        batterySignalMapper.deleteById(id);

        // 3. 延迟再删一次缓存，防止并发写回旧数据
        delayDelete(key);
        delayDelete(allKey);
    }

    private void delayDelete(String key) {
        new Thread(() -> {
            try {
                Thread.sleep(300); // 延迟 300ms 后再次删除缓存
                redisTemplate.delete(key);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

}
