package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.SensorData;
import cn.my.project.service.SensorDataService;
import cn.my.project.utils.JwtUtil;
import cn.my.project.utils.PermissionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private SensorDataService sensorDataService;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取实时监测数据（带区域权限检查）
     */
    @GetMapping("/realtime/{areaId}")
    public Result<Map<String, Object>> getRealtime(@PathVariable Long areaId, HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        // 检查区域权限
        if (!PermissionUtil.hasAreaPermission(userId, areaId, role)) {
            return Result.error("无权限访问该区域数据");
        }

        SensorData latest = sensorDataService.list(new LambdaQueryWrapper<SensorData>()
                .eq(SensorData::getAreaId, areaId)
                .orderByDesc(SensorData::getRecordTime)
                .last("limit 1")).stream().findFirst().orElse(null);

        Map<String, Object> result = new HashMap<>();
        if (latest != null) {
            result.put("temperature", latest.getTemperature());
            result.put("humidity", latest.getHumidity());
            result.put("pm25", latest.getPm25());
            result.put("windSpeed", latest.getWindSpeed());
            result.put("recordTime", latest.getRecordTime());
        }
        return Result.success(result);
    }

    /**
     * 获取趋势数据（带区域权限检查）
     * 支持两种模式：
     * 1. 基于小时数：hours参数，从当前时间往前推
     * 2. 基于日期范围：startDate和endDate参数，查询指定日期范围的数据
     */
    @GetMapping("/trend/{areaId}")
    public Result<Map<String, Object>> getTrend(@PathVariable Long areaId,
                                                 @RequestParam(defaultValue = "24") int hours,
                                                 @RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate,
                                                 HttpServletRequest request) {
        // 获取当前用户信息
        String token = extractToken(request);
        Long userId = JwtUtil.getUserId(token);
        String role = JwtUtil.getRole(token);

        // 检查区域权限
        if (!PermissionUtil.hasAreaPermission(userId, areaId, role)) {
            return Result.error("无权限访问该区域数据");
        }

        LocalDateTime startTime;
        LocalDateTime endTime = LocalDateTime.now();
        boolean useDateRange = startDate != null && endDate != null;

        if (useDateRange) {
            // 使用日期范围模式
            LocalDate start = LocalDate.parse(startDate, dateFormatter);
            LocalDate end = LocalDate.parse(endDate, dateFormatter);
            startTime = start.atStartOfDay();
            endTime = end.plusDays(1).atStartOfDay();
            // 计算小时数用于生成模拟数据
            hours = (int) java.time.Duration.between(startTime, endTime).toHours();
            if (hours < 1) hours = 1;
        } else {
            // 使用小时数模式
            startTime = LocalDateTime.now().minusHours(hours);
        }

        List<SensorData> dataList = sensorDataService.list(new LambdaQueryWrapper<SensorData>()
                .eq(SensorData::getAreaId, areaId)
                .ge(SensorData::getRecordTime, startTime)
                .lt(SensorData::getRecordTime, endTime)
                .orderByAsc(SensorData::getRecordTime));

        Map<String, Object> result = new HashMap<>();
        List<String> times = new ArrayList<>();
        List<Object> temps = new ArrayList<>();
        List<Object> hums = new ArrayList<>();
        List<Object> pm25s = new ArrayList<>();

        // 若真实数据不足，生成模拟数据填充时间窗口
        if (dataList.size() < 5) {
            int points = hours <= 24 ? 24 : Math.min(hours / 6, 30);
            java.util.Random rnd = new java.util.Random(areaId);
            double baseTemp = 20 + rnd.nextDouble() * 10;
            double baseHum = 50 + rnd.nextDouble() * 20;
            double basePm = 30 + rnd.nextDouble() * 30;

            for (int i = points; i >= 0; i--) {
                LocalDateTime t;
                if (useDateRange) {
                    // 使用日期范围计算时间点
                    long hoursPerPoint = hours / points;
                    t = startTime.plusHours(hoursPerPoint * (points - i));
                } else {
                    t = LocalDateTime.now().minusHours(hours <= 24 ? i : i * 6L);
                }

                // 根据时间跨度决定标签格式，确保每个时间点都有唯一标识
                String label;
                if (hours <= 24) {
                    // 24小时内，显示月/日 时:00
                    label = t.getMonthValue() + "/" + t.getDayOfMonth() + " " + String.format("%02d:00", t.getHour());
                } else if (hours <= 72) {
                    // 72小时内，显示月/日 时:00
                    label = t.getMonthValue() + "/" + t.getDayOfMonth() + " " + String.format("%02d:00", t.getHour());
                } else {
                    // 超过72小时，显示月/日，但如果同一天有多个点则加上时间
                    label = t.getMonthValue() + "/" + t.getDayOfMonth() + " " + String.format("%02d:00", t.getHour());
                }
                times.add(label);
                temps.add(Math.round((baseTemp + Math.sin(i * 0.3) * 3 + rnd.nextDouble() * 2) * 10.0) / 10.0);
                hums.add(Math.round((baseHum + Math.cos(i * 0.2) * 5 + rnd.nextDouble() * 3) * 10.0) / 10.0);
                pm25s.add(Math.round((basePm + Math.sin(i * 0.5) * 8 + rnd.nextDouble() * 5) * 10.0) / 10.0);
            }
        } else {
            // 使用真实数据
            java.util.Set<String> uniqueDates = new java.util.HashSet<>();
            for (SensorData data : dataList) {
                LocalDateTime t = data.getRecordTime();
                String dateStr = t.getMonthValue() + "/" + t.getDayOfMonth();
                uniqueDates.add(dateStr);
            }
            boolean multiplePointsPerDay = dataList.size() > uniqueDates.size();

            for (SensorData data : dataList) {
                LocalDateTime t = data.getRecordTime();
                // 根据数据的时间跨度和是否同一天有多个点决定标签格式
                String label;
                if (multiplePointsPerDay || hours <= 72) {
                    // 同一天有多个数据点或时间跨度较短，显示完整日期时间
                    label = t.getMonthValue() + "/" + t.getDayOfMonth() + " " + String.format("%02d:00", t.getHour());
                } else {
                    // 只显示日期
                    label = t.getMonthValue() + "/" + t.getDayOfMonth();
                }
                times.add(label);
                temps.add(data.getTemperature());
                hums.add(data.getHumidity());
                pm25s.add(data.getPm25());
            }
        }

        result.put("times", times);
        result.put("temperature", temps);
        result.put("humidity", hums);
        result.put("pm25", pm25s);
        return Result.success(result);
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
