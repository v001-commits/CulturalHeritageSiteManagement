package cn.my.project.utils;

import cn.my.project.entity.UserAreaPermission;
import cn.my.project.service.UserAreaPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限检查工具类
 */
@Component
public class PermissionUtil {

    private static UserAreaPermissionService userAreaPermissionService;

    @Autowired
    public void setUserAreaPermissionService(UserAreaPermissionService service) {
        PermissionUtil.userAreaPermissionService = service;
    }

    /**
     * 检查用户是否有权限访问指定区域
     * @param userId 用户ID
     * @param areaId 区域ID
     * @param role 用户角色
     * @return true-有权限，false-无权限
     */
    public static boolean hasAreaPermission(Long userId, Long areaId, String role) {
        // 系统管理员拥有所有权限
        if ("admin".equals(role)) {
            return true;
        }

        // 其他角色需要检查区域权限（不限制权限类型，只要有任何权限即可）
        List<Long> areaIds = userAreaPermissionService.getUserAreaIds(userId);
        return areaIds.contains(areaId);
    }

    /**
     * 获取用户有权限的区域ID列表
     * @param userId 用户ID
     * @param role 用户角色
     * @return 区域ID列表
     */
    public static List<Long> getUserAreaIds(Long userId, String role) {
        // 系统管理员拥有所有权限，返回null表示不限制
        if ("admin".equals(role)) {
            return null;
        }

        // 其他角色返回授权的区域ID列表
        return userAreaPermissionService.getUserAreaIds(userId);
    }

    /**
     * 检查用户是否可以操作数据（监测人员）
     * @param role 用户角色
     * @return true-可以操作，false-不可以操作
     */
    public static boolean canOperateData(String role) {
        return "admin".equals(role) || "monitor".equals(role);
    }

    /**
     * 检查用户是否可以分析数据（科研人员）
     * @param role 用户角色
     * @return true-可以分析，false-不可以分析
     */
    public static boolean canAnalyzeData(String role) {
        return "admin".equals(role) || "researcher".equals(role);
    }

    /**
     * 检查用户是否只能查看数据（遗产地管理员）
     * @param role 用户角色
     * @return true-只能查看，false-可以操作
     */
    public static boolean isViewOnly(String role) {
        return "manager".equals(role);
    }

    /**
     * 检查用户是否可以处理预警
     * @param role 用户角色
     * @return true-可以处理，false-不可以处理
     */
    public static boolean canHandleAlert(String role) {
        return "admin".equals(role) || "monitor".equals(role);
    }
}
