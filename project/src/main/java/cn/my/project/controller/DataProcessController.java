package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.ProcessedData;
import cn.my.project.entity.SensorData;
import cn.my.project.service.ProcessedDataService;
import cn.my.project.service.SensorDataService;
import cn.my.project.utils.JwtUtil;
import cn.my.project.utils.PermissionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/data")
public class DataProcessController {

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private ProcessedDataService processedDataService;

    /**
     * 处理数据（需要科研人员或管理员权限）
     */
    @PostMapping("/process/{id}")
    public Result<String> processData(@PathVariable Long id, HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        // 检查是否有数据处理权限（科研人员或管理员）
        if (!PermissionUtil.canAnalyzeData(role)) {
            return Result.error("仅系统管理员和科研人员可以处理数据");
        }

        SensorData source = sensorDataService.getById(id);
        if (source == null) {
            return Result.error("数据不存在");
        }

        // 检查区域权限
        if (!PermissionUtil.hasAreaPermission(userId, source.getAreaId(), role)) {
            return Result.error("无权限处理该区域数据");
        }

        ProcessedData processed = new ProcessedData();
        processed.setSourceId(id);
        processed.setAreaId(source.getAreaId());
        processed.setDataSource(source.getDataSource());
        processed.setTemperature(source.getTemperature());
        processed.setHumidity(source.getHumidity());
        processed.setPm25(source.getPm25());
        processed.setRecordTime(source.getRecordTime());
        processed.setProcessTime(LocalDateTime.now());

        // 异常检测
        boolean isAnomaly = false;
        String reason = "";
        if (source.getTemperature() != null && (source.getTemperature().compareTo(new BigDecimal("40")) > 0 || source.getTemperature().compareTo(new BigDecimal("-10")) < 0)) {
            isAnomaly = true;
            reason = "温度异常";
        }
        if (source.getHumidity() != null && (source.getHumidity().compareTo(new BigDecimal("100")) > 0 || source.getHumidity().compareTo(BigDecimal.ZERO) < 0)) {
            isAnomaly = true;
            reason += (reason.isEmpty() ? "" : ",") + "湿度异常";
        }

        processed.setIsAnomaly(isAnomaly ? 1 : 0);
        processed.setAnomalyReason(isAnomaly ? reason : null);
        processed.setProcessStatus("processed");
        processed.setVersion(1);
        processedDataService.save(processed);
        // 删除原始数据，避免重复显示在待处理列表中
        sensorDataService.removeById(id);
        return Result.success("处理完成");
    }

    /**
     * 获取已处理数据列表（带区域权限过滤）
     */
    @GetMapping("/processed/list")
    public Result<List<ProcessedData>> listProcessed(@RequestParam(required = false) Long areaId,
                                                       HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        LambdaQueryWrapper<ProcessedData> wrapper = new LambdaQueryWrapper<>();
        
        if (areaId != null && areaId > 0) {
            // 检查区域权限
            if (!PermissionUtil.hasAreaPermission(userId, areaId, role)) {
                return Result.error("无权限访问该区域数据");
            }
            wrapper.eq(ProcessedData::getAreaId, areaId);
        } else {
            // 过滤用户有权限的区域
            List<Long> areaIds = PermissionUtil.getUserAreaIds(userId, role);
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(ProcessedData::getAreaId, areaIds);
            } else if (areaIds != null) {
                return Result.success(new ArrayList<>());
            }
        }
        
        wrapper.orderByDesc(ProcessedData::getProcessTime);
        return Result.success(processedDataService.list(wrapper));
    }

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
}
