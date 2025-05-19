package com.wzh.vehicle_battery_alert.controller;

import com.wzh.vehicle_battery_alert.common.Result;
import com.wzh.vehicle_battery_alert.dto.WarnDTO;
import com.wzh.vehicle_battery_alert.service.WarnService;
import com.wzh.vehicle_battery_alert.vo.WarnVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@RestController
@RequestMapping("/api/warn")
public class WarnController {

    @Autowired
    private WarnService warnService;

    @PostMapping
    public Result<List<WarnVO>> warnEvaluate(@RequestBody List<WarnDTO> request){
        List<WarnVO> warnVOS = warnService.warnEvaluate(request);
        return Result.ok(warnVOS);
    }
}
