package cn.my.project.interceptor;

import cn.my.project.service.UserAreaPermissionService;
import cn.my.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限拦截器 - 用于检查用户的区域访问权限
 * 注意：此拦截器在JwtInterceptor之后执行，确保token已验证
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserAreaPermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            return true; // 让JwtInterceptor处理
        }

        try {
            // 解析token获取用户信息
            Long userId = JwtUtil.getUserId(token);
            String role = JwtUtil.getRole(token);

            // 管理员拥有所有权限，直接放行
            if ("admin".equals(role)) {
                return true;
            }

            // 获取请求的区域ID参数
            String areaIdParam = request.getParameter("areaId");
            if (areaIdParam == null || areaIdParam.isEmpty()) {
                // 如果没有区域参数，放行（由业务层处理）
                return true;
            }

            Long areaId = Long.parseLong(areaIdParam);

            // 检查用户是否有该区域的权限
            boolean hasPermission = permissionService.hasPermission(userId, areaId);
            
            if (!hasPermission) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"无权访问该区域\"}");
                return false;
            }

            return true;
        } catch (Exception e) {
            return true; // 让其他拦截器处理异常
        }
    }
}
