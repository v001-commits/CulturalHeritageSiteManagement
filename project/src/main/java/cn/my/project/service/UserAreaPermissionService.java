package cn.my.project.service;

import cn.my.project.entity.UserAreaPermission;
import cn.my.project.mapper.UserAreaPermissionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAreaPermissionService extends ServiceImpl<UserAreaPermissionMapper, UserAreaPermission> {
    
    /**
     * 获取用户的所有区域权限
     */
    public List<UserAreaPermission> getUserPermissions(Long userId) {
        return this.list(new LambdaQueryWrapper<UserAreaPermission>()
                .eq(UserAreaPermission::getUserId, userId));
    }
    
    /**
     * 获取区域的所有用户权限
     */
    public List<UserAreaPermission> getAreaPermissions(Long areaId) {
        return this.list(new LambdaQueryWrapper<UserAreaPermission>()
                .eq(UserAreaPermission::getAreaId, areaId));
    }
    
    /**
     * 检查用户是否有区域权限
     */
    public boolean hasPermission(Long userId, Long areaId) {
        return this.count(new LambdaQueryWrapper<UserAreaPermission>()
                .eq(UserAreaPermission::getUserId, userId)
                .eq(UserAreaPermission::getAreaId, areaId)) > 0;
    }
    
    /**
     * 删除用户的所有权限
     */
    public boolean deleteUserPermissions(Long userId) {
        return this.remove(new LambdaQueryWrapper<UserAreaPermission>()
                .eq(UserAreaPermission::getUserId, userId));
    }
    
    /**
     * 删除区域的所有权限
     */
    public boolean deleteAreaPermissions(Long areaId) {
        return this.remove(new LambdaQueryWrapper<UserAreaPermission>()
                .eq(UserAreaPermission::getAreaId, areaId));
    }
    
    /**
     * 获取用户有权限的区域ID列表
     */
    public List<Long> getUserAreaIds(Long userId) {
        List<UserAreaPermission> permissions = this.list(new LambdaQueryWrapper<UserAreaPermission>()
                .eq(UserAreaPermission::getUserId, userId));
        return permissions.stream()
                .map(UserAreaPermission::getAreaId)
                .collect(java.util.stream.Collectors.toList());
    }
}
