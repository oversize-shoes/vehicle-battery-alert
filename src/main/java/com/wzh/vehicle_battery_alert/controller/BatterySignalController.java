package com.wzh.vehicle_battery_alert.controller;

import com.wzh.vehicle_battery_alert.common.Result;
import com.wzh.vehicle_battery_alert.model.BatterySignal;
import com.wzh.vehicle_battery_alert.service.BatterySignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@RestController
@RequestMapping("/api/signal")
public class BatterySignalController {
    @Autowired
    private BatterySignalService batterySignalService;

    @PostMapping
    public Result<String> insert(@RequestBody BatterySignal batterySignal){
        batterySignalService.insert(batterySignal);
        return Result.ok("添加成功");
    }

    @GetMapping("/{id}")//根据车架编号查询
    public Result<BatterySignal> get(@PathVariable Long id) {
        BatterySignal signal = batterySignalService.getByVin(id);
        return signal != null ? Result.ok(signal) : Result.fail("信号不存在");
    }

    @GetMapping("/all/{id}")
    public Result<List<BatterySignal>> list(@PathVariable Long id) {
        return Result.ok(batterySignalService.listAll(id));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        batterySignalService.delete(id);
        return Result.ok("删除成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody BatterySignal signal) {
        batterySignalService.update(signal);
        return Result.ok("更新成功");
    }
}
