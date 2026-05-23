package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.AlertRecord;
import cn.my.project.service.AlertRecordService;
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
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    private AlertRecordService alertRecordService;

    /**
     * 获取预警记录（带区域权限过滤）
     */
    @GetMapping("/records")
    public Result<List<AlertRecord>> getRecords(@RequestParam(required = false) String status,
                                                @RequestParam(required = false) Long areaId,
                                                HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        LambdaQueryWrapper<AlertRecord> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(AlertRecord::getStatus, status);
        }

        // 如果指定了区域ID，按指定区域过滤
        if (areaId != null) {
            // 检查用户是否有该区域的权限
            if (!PermissionUtil.hasAreaPermission(userId, areaId, role)) {
                return Result.success(new ArrayList<>());
            }
            wrapper.eq(AlertRecord::getAreaId, areaId);
        } else {
            // 过滤用户有权限的区域
            List<Long> areaIds = PermissionUtil.getUserAreaIds(userId, role);
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(AlertRecord::getAreaId, areaIds);
            } else if (areaIds != null) {
                // 空列表表示没有任何权限
                return Result.success(new ArrayList<>());
            }
            // null表示管理员，不限制
        }

        wrapper.orderByDesc(AlertRecord::getCreateTime);
        return Result.success(alertRecordService.list(wrapper));
    }

    /**
     * 处理预警（需要权限）
     */
    @PostMapping("/process/{id}")
    public Result<String> processAlert(@PathVariable Long id, HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        // 检查是否有处理预警的权限（管理员或监测人员）
        if (!PermissionUtil.canHandleAlert(role)) {
            return Result.error("仅系统管理员和监测人员可以处理预警");
        }

        AlertRecord record = alertRecordService.getById(id);
        if (record == null) {
            return Result.error("预警记录不存在");
        }

        // 检查区域权限
        if (!PermissionUtil.hasAreaPermission(userId, record.getAreaId(), role)) {
            return Result.error("无权限处理该区域的预警");
        }

        record.setStatus("processed");
        alertRecordService.updateById(record);
        return Result.success("处理成功");
    }

    /**
     * 添加预警（仅管理员）
     */
    @PostMapping("/add")
    public Result<String> addAlert(@RequestBody AlertRecord alert, HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        String role = JwtUtil.getRole(token);

        // 仅管理员可以添加预警
        if (!"admin".equals(role)) {
            return Result.error("仅系统管理员可以添加预警");
        }

        alert.setStatus("pending");
        alert.setCreateTime(LocalDateTime.now());
        alertRecordService.save(alert);
        return Result.success("添加成功");
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
