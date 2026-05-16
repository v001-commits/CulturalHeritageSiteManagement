package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.OperationLog;
import cn.my.project.service.OperationLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class OperationLogController {

    @Autowired
    private OperationLogService logService;

    /**
     * 获取操作日志列表（分页）
     */
    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "20") Integer size,
                          @RequestParam(required = false) String operationType) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (operationType != null && !operationType.isEmpty()) {
            wrapper.eq(OperationLog::getOperationType, operationType);
        }
        
        wrapper.orderByDesc(OperationLog::getCreateTime);
        
        Page<OperationLog> pageResult = logService.page(new Page<>(page, size), wrapper);
        return Result.success(pageResult);
    }

    /**
     * 根据用户ID获取操作日志
     */
    @GetMapping("/user/{userId}")
    public Result<?> getUserLogs(@PathVariable Long userId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getUserId, userId)
                .orderByDesc(OperationLog::getCreateTime);
        return Result.success(logService.list(wrapper));
    }
}
