package cn.my.project.interceptor;

import cn.my.project.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            writeError(response, 401, "未登录，请先登录");
            return false;
        }

        try {
            JwtUtil.parse(token);
        } catch (JwtException e) {
            writeError(response, 401, "token无效或已过期");
            return false;
        }

        String role = JwtUtil.getRole(token);
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // ========== 系统管理员专属权限 ==========
        // 用户管理（CRUD）
        if (uri.startsWith("/user") && !uri.equals("/user/current")) {
            if (!"admin".equals(role)) {
                writeError(response, 403, "仅系统管理员可管理用户");
                return false;
            }
        }

        // 操作日志查看
        if (uri.startsWith("/log")) {
            if (!"admin".equals(role)) {
                writeError(response, 403, "仅系统管理员可查看操作日志");
                return false;
            }
        }

        // 区域管理（写操作）
        if (uri.startsWith("/area") && !uri.contains("/permission") && !uri.contains("/user/") && 
            (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"))) {
            if (!"admin".equals(role)) {
                writeError(response, 403, "仅系统管理员可管理区域");
                return false;
            }
        }

        // 权限分配
        if (uri.startsWith("/area/permission") && (method.equals("POST") || method.equals("DELETE"))) {
            if (!"admin".equals(role)) {
                writeError(response, 403, "仅系统管理员可分配权限");
                return false;
            }
        }

        // 阈值配置（写操作）
        if (uri.startsWith("/threshold") && (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"))) {
            if (!"admin".equals(role)) {
                writeError(response, 403, "仅系统管理员可配置阈值");
                return false;
            }
        }

        // ========== 监测人员权限 ==========
        // 数据采集（传感器数据写入）
        if (uri.startsWith("/sensor") && (method.equals("POST") || method.equals("DELETE"))) {
            if (!"admin".equals(role) && !"monitor".equals(role)) {
                writeError(response, 403, "仅系统管理员和监测人员可采集数据");
                return false;
            }
        }

        // 设备维护
        if (uri.startsWith("/device") && (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"))) {
            if (!"admin".equals(role) && !"monitor".equals(role)) {
                writeError(response, 403, "仅系统管理员和监测人员可维护设备");
                return false;
            }
        }

        // ========== 科研人员权限 ==========
        // 数据处理
        if (uri.startsWith("/data/process")) {
            if (!"admin".equals(role) && !"researcher".equals(role)) {
                writeError(response, 403, "仅系统管理员和科研人员可处理数据");
                return false;
            }
        }

        // 统计分析
        if (uri.startsWith("/statistics") && method.equals("POST")) {
            if (!"admin".equals(role) && !"researcher".equals(role)) {
                writeError(response, 403, "仅系统管理员和科研人员可生成报告");
                return false;
            }
        }

        // ========== 遗产地管理员权限 ==========
        // 遗产地管理员只能查看数据和预警，不能修改
        // 所有写操作都需要检查角色
        if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
            // 预警处理（标记已处理）- 管理员和监测人员可操作
            if (uri.startsWith("/alert/process")) {
                if (!"admin".equals(role) && !"monitor".equals(role)) {
                    writeError(response, 403, "仅系统管理员和监测人员可处理预警");
                    return false;
                }
            }
            // 添加预警 - 仅管理员
            else if (uri.startsWith("/alert/add")) {
                if (!"admin".equals(role)) {
                    writeError(response, 403, "仅系统管理员可添加预警");
                    return false;
                }
            }
        }

        // ========== 区域权限检查 ==========
        // 对于需要区域权限的接口，在Controller层进一步检查
        // 这里只做角色级别的权限控制

        return true;
    }

    private void writeError(HttpServletResponse response, int status, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        Map<String, Object> map = new HashMap<>();
        map.put("code", status);
        map.put("msg", msg);
        response.getWriter().write(new ObjectMapper().writeValueAsString(map));
    }
}

