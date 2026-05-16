package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.MonitorArea;
import cn.my.project.service.MonitorAreaService;
import cn.my.project.utils.JwtUtil;
import cn.my.project.utils.PermissionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/area")
public class AreaController {

    @Autowired
    private MonitorAreaService areaService;

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
     * 获取所有区域（仅管理员使用）
     */
    @GetMapping("/list")
    public Result<List<MonitorArea>> list() {
        return Result.success(areaService.list());
    }

    /**
     * 获取当前用户有权限访问的区域列表
     */
    @GetMapping("/user/authorized")
    public Result<List<MonitorArea>> getUserAuthorizedAreas(HttpServletRequest request) {
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        // 管理员返回所有区域
        if ("admin".equals(role)) {
            return Result.success(areaService.list());
        }

        // 其他用户只返回有权限的区域
        List<Long> areaIds = PermissionUtil.getUserAreaIds(userId, role);
        if (areaIds == null || areaIds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<MonitorArea> allAreas = areaService.list();
        List<MonitorArea> authorizedAreas = allAreas.stream()
                .filter(area -> areaIds.contains(area.getId()))
                .collect(Collectors.toList());
        return Result.success(authorizedAreas);
    }

    /**
     * 根据ID获取区域
     */
    @GetMapping("/{id}")
    public Result<MonitorArea> getById(@PathVariable Long id) {
        return Result.success(areaService.getById(id));
    }

    /**
     * 添加区域
     */
    @PostMapping
    public Result<?> add(@RequestBody MonitorArea area) {
        area.setCreateTime(LocalDateTime.now());
        return areaService.save(area) ?
                Result.success("添加区域成功") : Result.error("添加区域失败");
    }

    /**
     * 更新区域
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody MonitorArea area) {
        area.setId(id);
        return areaService.updateById(area) ?
                Result.success("更新区域成功") : Result.error("更新区域失败");
    }

    /**
     * 删除区域
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return areaService.removeById(id) ?
                Result.success("删除区域成功") : Result.error("删除区域失败");
    }
}
