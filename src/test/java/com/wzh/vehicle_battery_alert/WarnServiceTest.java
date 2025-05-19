package com.wzh.vehicle_battery_alert;

import com.wzh.vehicle_battery_alert.dto.WarnDTO;
import com.wzh.vehicle_battery_alert.mapper.VehicleMapper;
import com.wzh.vehicle_battery_alert.model.BatterySignal;
import com.wzh.vehicle_battery_alert.model.Vehicle;
import com.wzh.vehicle_battery_alert.service.BatterySignalService;
import com.wzh.vehicle_battery_alert.service.WarnService;
import com.wzh.vehicle_battery_alert.vo.WarnVO;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

/**
 * @author WZH
 * @date 2025/5/18
 **/
@SpringBootTest
public class WarnServiceTest {
    @Autowired
    private WarnService warnService;
    @Autowired
    private VehicleMapper vehicleMapper;
    @Autowired
    private BatterySignalService batterySignalService;

    @Test
    public void testEvaluate_ALL() {
        WarnDTO dto = new WarnDTO();
        dto.setCarId(9L);
        dto.setSignal("{\"Mx\":4.0,\"Mi\":3.2,\"Ix\":120.0,\"Ii\":118.7}");

        List<WarnDTO> warnDTO = Collections.singletonList(dto);


        List<WarnVO> result = warnService.warnEvaluate(warnDTO);
        System.out.println(result);

    }

    @Test
    public void testEvaluate_Voltage() {
        WarnDTO dto = new WarnDTO();
        dto.setCarId(1007L);
        dto.setWarnId(1L);
        dto.setSignal("{\"Mx\":12.0,\"Mi\":11.9}");

        List<WarnDTO> warnDTO = Collections.singletonList(dto);


        List<WarnVO> result = warnService.warnEvaluate(warnDTO);
        System.out.println(result);

    }

    @Test
    public void testEvaluate_Current() {
        WarnDTO dto = new WarnDTO();
        dto.setCarId(1002L);
        dto.setWarnId(2L);
        dto.setSignal("{\"Ix\":12.0,\"Ii\":11.7}");

        List<WarnDTO> warnDTO = Collections.singletonList(dto);


        List<WarnVO> result = warnService.warnEvaluate(warnDTO);
        System.out.println(result);

    }
    @Test
    public void testEvaluate_Wrong1() {
        //参数传入错误
        //{"Ix":12.0,"Ii":11.7}应该匹配规则2（电流规则）
        WarnDTO dto = new WarnDTO();
        dto.setCarId(1002L);
        dto.setWarnId(1L);
        dto.setSignal("{\"Ix\":12.0,\"Ii\":11.7}");

        List<WarnDTO> warnDTO = Collections.singletonList(dto);


        List<WarnVO> result = warnService.warnEvaluate(warnDTO);
        System.out.println(result);

    }

    @Test
    public void testEvaluate_Wrong2() {
        //参数传入错误
        //Ix < Ii
        WarnDTO dto = new WarnDTO();
        dto.setCarId(1002L);
        dto.setWarnId(2L);
        dto.setSignal("{\"Ix\":10.0,\"Ii\":11.7}");

        List<WarnDTO> warnDTO = Collections.singletonList(dto);


        List<WarnVO> result = warnService.warnEvaluate(warnDTO);
        System.out.println(result);

    }

    @Test
    public void testEvaluate_Wrong3() {
        //参数传入错误
        //传入abc等不符合要求的字段或缺失字段
        WarnDTO dto = new WarnDTO();
        dto.setCarId(1002L);
        dto.setWarnId(2L);
        dto.setSignal("{\"Ix\":Abc}");

        List<WarnDTO> warnDTO = Collections.singletonList(dto);


        List<WarnVO> result = warnService.warnEvaluate(warnDTO);
        System.out.println(result);

    }

    @Test
    public void testInsertVehicle() {
//        for (long vin = 1001; vin < 1010; vin++) {
//            Vehicle v = new Vehicle();
//            v.setVid("ABC123456BC"+vin);
//            v.setVin(vin);
//            v.setBatteryType("三元电池");
//            v.setMileage(12345);
//            v.setHealth(95);
//            vehicleMapper.insert(v);
//        }
        Vehicle v = new Vehicle();
        v.setVid("ABC123456BC"+9);
        v.setVin(9L);
        v.setBatteryType("三元电池");
        v.setMileage(12345);
        v.setHealth(95);
        vehicleMapper.insert(v);
    }

    @Test
    public void selectSignal(){
        BatterySignal byVin = batterySignalService.getByVin(1007L);
        System.out.println(byVin);
    }
}
