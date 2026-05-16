package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.SensorData;
import cn.my.project.service.SensorDataService;
import cn.my.project.service.UserAreaPermissionService;
import cn.my.project.utils.JwtUtil;
import cn.my.project.utils.PermissionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sensor")
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private UserAreaPermissionService userAreaPermissionService;

    /**
     * 获取传感器数据列表（带区域权限过滤）
     */
    @GetMapping("/list/{areaId}")
    public Result<List<SensorData>> list(@PathVariable Long areaId, HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        // 检查区域权限
        if (areaId != null && areaId > 0) {
            if (!PermissionUtil.hasAreaPermission(userId, areaId, role)) {
                return Result.error("无权限访问该区域数据");
            }
        }

        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<>();
        if (areaId != null && areaId > 0) {
            wrapper.eq(SensorData::getAreaId, areaId);
        } else {
            // 如果不指定区域，则只返回用户有权限的区域数据
            List<Long> areaIds = PermissionUtil.getUserAreaIds(userId, role);
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(SensorData::getAreaId, areaIds);
            } else if (areaIds != null) {
                // 空列表表示没有任何权限
                return Result.success(new ArrayList<>());
            }
            // null表示管理员，不限制
        }
        
        wrapper.orderByDesc(SensorData::getRecordTime);
        return Result.success(sensorDataService.list(wrapper));
    }

    /**
     * 添加传感器数据（需要区域权限）
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody SensorData data, HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        // 检查是否有操作权限（监测人员或管理员）
        if (!PermissionUtil.canOperateData(role)) {
            return Result.error("仅监测人员和系统管理员可以添加数据");
        }

        // 检查区域权限
        if (!PermissionUtil.hasAreaPermission(userId, data.getAreaId(), role)) {
            return Result.error("无权限操作该区域数据");
        }

        if (data.getRecordTime() == null) {
            data.setRecordTime(LocalDateTime.now());
        }
        data.setCreateTime(LocalDateTime.now());
        sensorDataService.save(data);
        return Result.success("数据添加成功");
    }

    /**
     * 按数据来源查询（带区域权限过滤）
     */
    @GetMapping("/sources")
    public Result<List<SensorData>> listBySource(@RequestParam String source, HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensorData::getDataSource, source);

        // 过滤用户有权限的区域
        List<Long> areaIds = PermissionUtil.getUserAreaIds(userId, role);
        if (areaIds != null && !areaIds.isEmpty()) {
            wrapper.in(SensorData::getAreaId, areaIds);
        } else if (areaIds != null) {
            return Result.success(new ArrayList<>());
        }

        wrapper.orderByDesc(SensorData::getRecordTime);
        return Result.success(sensorDataService.list(wrapper));
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
