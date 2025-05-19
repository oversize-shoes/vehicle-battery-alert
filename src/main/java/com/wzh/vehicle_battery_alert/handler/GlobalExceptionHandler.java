package com.wzh.vehicle_battery_alert.handler;

import com.wzh.vehicle_battery_alert.common.IdempotentException;
import com.wzh.vehicle_battery_alert.common.ParamsException;
import com.wzh.vehicle_battery_alert.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author WZH
 * @date 2025/5/19
 **/

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdempotentException.class)
    public Result<?> handleIdempotentException(IdempotentException e) {
        return Result.fail(409, e.getMessage()); // 409 Conflict
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        return Result.fail(500, e.getMessage());
    }

    @ExceptionHandler(ParamsException.class)
    public Result<?> handleBizException(ParamsException e) {
        return Result.fail(e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public Result<?> handleGeneralException(Exception e) {
        return Result.fail("系统错误：" + e.getMessage());
    }
}
