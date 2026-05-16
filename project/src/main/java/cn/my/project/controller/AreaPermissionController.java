package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.UserAreaPermission;
import cn.my.project.service.UserAreaPermissionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/area/permission")
public class AreaPermissionController {

    @Autowired
    private UserAreaPermissionService permissionService;

    /**
     * 获取所有权限
     */
    @GetMapping("/all")
    public Result<List<UserAreaPermission>> getAllPermissions() {
        return Result.success(permissionService.list());
    }

    /**
     * 获取用户的权限列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<UserAreaPermission>> getUserPermissions(@PathVariable Long userId) {
        return Result.success(permissionService.getUserPermissions(userId));
    }

    /**
     * 获取区域的权限列表
     */
    @GetMapping("/area/{areaId}")
    public Result<List<UserAreaPermission>> getAreaPermissions(@PathVariable Long areaId) {
        return Result.success(permissionService.getAreaPermissions(areaId));
    }

    /**
     * 添加权限
     */
    @PostMapping
    public Result<?> addPermission(@RequestBody PermissionRequest req) {
        // 检查是否已存在
        if (permissionService.hasPermission(req.getUserId(), req.getAreaId())) {
            return Result.error("该用户已有此区域权限");
        }
        
        UserAreaPermission permission = new UserAreaPermission();
        permission.setUserId(req.getUserId());
        permission.setAreaId(req.getAreaId());
        permission.setPermissionType(req.getPermissionType());
        permission.setCreateTime(LocalDateTime.now());
        
        return permissionService.save(permission) ? 
                Result.success("添加权限成功") : Result.error("添加权限失败");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<?> deletePermission(@PathVariable Long id) {
        return permissionService.removeById(id) ? 
                Result.success("删除权限成功") : Result.error("删除权限失败");
    }

    /**
     * 批量设置用户权限
     */
    @PostMapping("/batch")
    public Result<?> batchSetPermissions(@RequestBody BatchPermissionRequest req) {
        // 先删除用户所有权限
        permissionService.deleteUserPermissions(req.getUserId());
        
        // 添加新权限
        for (Long areaId : req.getAreaIds()) {
            UserAreaPermission permission = new UserAreaPermission();
            permission.setUserId(req.getUserId());
            permission.setAreaId(areaId);
            permission.setPermissionType(req.getPermissionType());
            permission.setCreateTime(LocalDateTime.now());
            permissionService.save(permission);
        }
        
        return Result.success("设置权限成功");
    }

    @Data
    static class PermissionRequest {
        private Long userId;
        private Long areaId;
        private String permissionType;
    }

    @Data
    static class BatchPermissionRequest {
        private Long userId;
        private List<Long> areaIds;
        private String permissionType;
    }
}
