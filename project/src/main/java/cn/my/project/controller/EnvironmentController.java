package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.EnvironmentData;
import cn.my.project.entity.MonitorDevice;
import cn.my.project.entity.HistoryData;
import cn.my.project.service.EnvironmentDataService;
import cn.my.project.service.MonitorDeviceService;
import cn.my.project.service.HistoryDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/environment")
public class EnvironmentController {

    @Autowired
    private EnvironmentDataService environmentDataService;

    @Autowired
    private MonitorDeviceService deviceService;

    @Autowired
    private HistoryDataService historyDataService;

    @GetMapping("/list")
    public Result<List<EnvironmentData>> list() {
        return Result.success(environmentDataService.list());
    }

    @GetMapping("/devices/{areaId}")
    public Result<List<MonitorDevice>> getDevices(@PathVariable Long areaId) {
        return Result.success(deviceService.list(new LambdaQueryWrapper<MonitorDevice>().eq(MonitorDevice::getAreaId, areaId)));
    }

    @GetMapping("/history/{areaId}")
    public Result<List<HistoryData>> getHistory(@PathVariable Long areaId) {
        return Result.success(historyDataService.list(new LambdaQueryWrapper<HistoryData>().eq(HistoryData::getAreaId, areaId).orderByDesc(HistoryData::getRecordTime)));
    }
}
