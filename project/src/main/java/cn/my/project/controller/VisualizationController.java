package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.EnvironmentDataRealtime;
import cn.my.project.entity.MonitorArea;
import cn.my.project.entity.MonitorDevice;
import cn.my.project.service.EnvironmentDataRealtimeService;
import cn.my.project.service.MonitorAreaService;
import cn.my.project.service.MonitorDeviceService;
import cn.my.project.utils.JwtUtil;
import cn.my.project.utils.PermissionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 三维可视化和二维地图控制器
 */
@RestController
@RequestMapping("/visualization")
public class VisualizationController {

    @Autowired
    private MonitorAreaService monitorAreaService;

    @Autowired
    private MonitorDeviceService monitorDeviceService;

    @Autowired
    private EnvironmentDataRealtimeService environmentDataRealtimeService;

    /**
     * 从请求中提取Token
     */
    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    /**
     * 获取所有区域及其设备信息（用于地图标注）
     * 根据用户权限过滤区域
     */
    @GetMapping("/areas-with-devices")
    public Result getAreasWithDevices(HttpServletRequest request) {
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        // 获取所有区域
        List<MonitorArea> allAreas = monitorAreaService.list();

        // 根据用户权限过滤区域
        List<MonitorArea> areas;
        if ("admin".equals(role)) {
            // 管理员显示所有区域
            areas = allAreas;
        } else {
            // 其他用户只显示有权限的区域
            List<Long> areaIds = PermissionUtil.getUserAreaIds(userId, role);
            if (areaIds == null || areaIds.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            areas = allAreas.stream()
                    .filter(area -> areaIds.contains(area.getId()))
                    .collect(Collectors.toList());
        }
        List<Map<String, Object>> result = areas.stream().map(area -> {
            Map<String, Object> areaMap = new HashMap<>();
            areaMap.put("id", area.getId());
            areaMap.put("areaName", area.getAreaName());
            areaMap.put("areaCode", area.getAreaCode());
            areaMap.put("description", area.getDescription());
            areaMap.put("latitude", area.getLatitude());
            areaMap.put("longitude", area.getLongitude());
            areaMap.put("altitude", area.getAltitude());
            areaMap.put("boundaryPoints", area.getBoundaryPoints());
            areaMap.put("heritageType", area.getHeritageType());
            areaMap.put("modelUrl", area.getModelUrl());
            areaMap.put("cameraPosition", area.getCameraPosition());
            areaMap.put("riskLevel", area.getRiskLevel());
            
            // 获取该区域的设备列表
            List<MonitorDevice> devices = monitorDeviceService.list(
                new LambdaQueryWrapper<MonitorDevice>().eq(MonitorDevice::getAreaId, area.getId())
            );
            areaMap.put("devices", devices);
            
            // 获取该区域的最新环境数据
            EnvironmentDataRealtime latestData = environmentDataRealtimeService.getLatestByAreaId(area.getId());
            areaMap.put("latestData", latestData);
            
            return areaMap;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }

    /**
     * 获取指定区域的详细信息
     */
    @GetMapping("/area/{areaId}")
    public Result getAreaDetail(@PathVariable Long areaId) {
        MonitorArea area = monitorAreaService.getById(areaId);
        if (area == null) {
            return Result.error("区域不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("area", area);
        
        // 获取该区域的所有设备
        List<MonitorDevice> devices = monitorDeviceService.list(
            new LambdaQueryWrapper<MonitorDevice>().eq(MonitorDevice::getAreaId, areaId)
        );
        result.put("devices", devices);
        
        // 获取最新环境数据
        EnvironmentDataRealtime latestData = environmentDataRealtimeService.getLatestByAreaId(areaId);
        result.put("latestData", latestData);
        
        return Result.success(result);
    }

    /**
     * 获取指定设备的详细信息和实时数据
     */
    @GetMapping("/device/{deviceId}")
    public Result getDeviceDetail(@PathVariable Long deviceId) {
        MonitorDevice device = monitorDeviceService.getById(deviceId);
        if (device == null) {
            return Result.error("设备不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("device", device);
        
        // 获取设备最新数据
        EnvironmentDataRealtime latestData = environmentDataRealtimeService.getLatestByDeviceId(deviceId);
        result.put("latestData", latestData);
        
        // 获取设备所属区域信息
        MonitorArea area = monitorAreaService.getById(device.getAreaId());
        result.put("area", area);
        
        return Result.success(result);
    }

    /**
     * 获取所有设备点位信息（用于地图标注）
     */
    @GetMapping("/devices")
    public Result getAllDevices() {
        List<MonitorDevice> devices = monitorDeviceService.list();
        return Result.success(devices);
    }

    /**
     * 更新设备风险等级
     */
    @PostMapping("/device/{deviceId}/risk-level")
    public Result updateDeviceRiskLevel(@PathVariable Long deviceId, @RequestParam String riskLevel) {
        MonitorDevice device = monitorDeviceService.getById(deviceId);
        if (device == null) {
            return Result.error("设备不存在");
        }
        
        device.setRiskLevel(riskLevel);
        monitorDeviceService.updateById(device);
        
        return Result.success("更新成功");
    }

    /**
     * 更新区域风险等级
     */
    @PostMapping("/area/{areaId}/risk-level")
    public Result updateAreaRiskLevel(@PathVariable Long areaId, @RequestParam String riskLevel) {
        MonitorArea area = monitorAreaService.getById(areaId);
        if (area == null) {
            return Result.error("区域不存在");
        }
        
        area.setRiskLevel(riskLevel);
        monitorAreaService.updateById(area);
        
        return Result.success("更新成功");
    }

    /**
     * 获取所有实时环境数据
     */
    @GetMapping("/realtime-data")
    public Result getAllRealtimeData() {
        List<EnvironmentDataRealtime> dataList = environmentDataRealtimeService.getAllLatestData();
        return Result.success(dataList);
    }
    
    /**
     * 根据季节获取环境数据
     */
    @GetMapping("/season-data")
    public Result getSeasonData(@RequestParam String season) {
        // 根据季节筛选数据（这里简化处理，实际应根据recordTime的月份判断）
        List<EnvironmentDataRealtime> dataList = environmentDataRealtimeService.getAllLatestData();
        return Result.success(dataList);
    }
    
    /**
     * 根据时段获取环境数据
     */
    @GetMapping("/timeslot-data")
    public Result getTimeSlotData(@RequestParam String timeSlot, @RequestParam(required = false) Long areaId) {
        // 根据时段筛选数据（这里简化处理，实际应根据recordTime的小时判断）
        List<EnvironmentDataRealtime> dataList;
        if (areaId != null) {
            dataList = environmentDataRealtimeService.list(
                new LambdaQueryWrapper<EnvironmentDataRealtime>()
                    .eq(EnvironmentDataRealtime::getAreaId, areaId)
                    .orderByDesc(EnvironmentDataRealtime::getRecordTime)
            );
        } else {
            dataList = environmentDataRealtimeService.getAllLatestData();
        }
        return Result.success(dataList);
    }

    /**
     * 保存或更新实时环境数据
     */
    @PostMapping("/realtime-data")
    public Result saveRealtimeData(@RequestBody EnvironmentDataRealtime data) {
        // 先查找是否存在该区域或设备的数据
        EnvironmentDataRealtime existing = null;
        if (data.getDeviceId() != null) {
            existing = environmentDataRealtimeService.getLatestByDeviceId(data.getDeviceId());
        } else if (data.getAreaId() != null) {
            existing = environmentDataRealtimeService.getLatestByAreaId(data.getAreaId());
        }
        
        if (existing != null) {
            // 更新现有数据
            data.setId(existing.getId());
            environmentDataRealtimeService.updateById(data);
        } else {
            // 插入新数据
            environmentDataRealtimeService.save(data);
        }
        
        return Result.success("保存成功");
    }
}
