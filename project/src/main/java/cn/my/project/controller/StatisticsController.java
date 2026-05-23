package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.MonitorArea;
import cn.my.project.service.MonitorAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/statistics")
@CrossOrigin
public class StatisticsController {

    @Autowired
    private MonitorAreaService areaService;

    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrend(@RequestParam Long areaId, @RequestParam String period) {
        Map<String, Object> data = new HashMap<>();
        List<String> xAxis = new ArrayList<>();
        List<Double> temperature = new ArrayList<>();
        List<Double> humidity = new ArrayList<>();
        List<Double> pm25 = new ArrayList<>();

        if ("month".equals(period)) {
            xAxis = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月");
            temperature = Arrays.asList(22.0, 23.0, 25.0, 27.0, 29.0, 28.0);
            humidity = Arrays.asList(55.0, 58.0, 60.0, 65.0, 70.0, 68.0);
            pm25 = Arrays.asList(35.0, 40.0, 45.0, 50.0, 55.0, 48.0);
        } else if ("quarter".equals(period)) {
            xAxis = Arrays.asList("Q1", "Q2", "Q3", "Q4");
            temperature = Arrays.asList(23.0, 27.0, 30.0, 25.0);
            humidity = Arrays.asList(57.0, 65.0, 72.0, 60.0);
            pm25 = Arrays.asList(40.0, 50.0, 60.0, 45.0);
        } else {
            xAxis = Arrays.asList("2021", "2022", "2023", "2024", "2025");
            temperature = Arrays.asList(24.0, 25.0, 26.0, 27.0, 28.0);
            humidity = Arrays.asList(60.0, 62.0, 65.0, 68.0, 70.0);
            pm25 = Arrays.asList(45.0, 48.0, 50.0, 52.0, 55.0);
        }

        data.put("xAxis", xAxis);
        data.put("temperature", temperature);
        data.put("humidity", humidity);
        data.put("pm25", pm25);
        return Result.success(data);
    }

    @GetMapping("/alert")
    public Result<Map<String, Object>> getAlertStats(@RequestParam Long areaId) {
        Map<String, Object> data = new HashMap<>();
        data.put("levels", Arrays.asList("低风险", "中风险", "高风险"));
        data.put("values", Arrays.asList(45, 30, 25));
        return Result.success(data);
    }

    /**
     * 区域对比接口
     * @param areaIds 逗号分隔的区域ID列表
     * @param timeType 时间类型: period(按周期) 或 date(按日期范围)
     * @param period 周期类型: month(月度), quarter(季度), year(年度)
     * @param startDate 开始日期 (当timeType为date时)
     * @param endDate 结束日期 (当timeType为date时)
     */
    @GetMapping("/compare")
    public Result<Map<String, Object>> getAreaCompare(
            @RequestParam String areaIds,
            @RequestParam String timeType,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Map<String, Object> result = new HashMap<>();
        List<String> areaNames = new ArrayList<>();
        List<Double> avgTemperatures = new ArrayList<>();
        List<Double> avgHumidity = new ArrayList<>();
        List<Double> avgPm25 = new ArrayList<>();

        // 解析区域ID列表
        String[] idArray = areaIds.split(",");
        Random random = new Random();

        for (String idStr : idArray) {
            try {
                Long areaId = Long.parseLong(idStr.trim());
                // 获取区域名称
                MonitorArea area = areaService.getById(areaId);
                String areaName = area != null ? area.getAreaName() : "区域" + areaId;
                areaNames.add(areaName);

                // 根据时间类型计算平均值
                if ("date".equals(timeType) && startDate != null && endDate != null) {
                    // 按日期范围计算
                    double[] avgs = calculateAverageByDateRange(areaId, startDate, endDate);
                    avgTemperatures.add(avgs[0]);
                    avgHumidity.add(avgs[1]);
                    avgPm25.add(avgs[2]);
                } else {
                    // 按周期计算 (默认月度)
                    String periodType = period != null ? period : "month";
                    double[] avgs = calculateAverageByPeriod(areaId, periodType);
                    avgTemperatures.add(avgs[0]);
                    avgHumidity.add(avgs[1]);
                    avgPm25.add(avgs[2]);
                }
            } catch (NumberFormatException e) {
                // 跳过无效ID
            }
        }

        result.put("areas", areaNames);
        result.put("temperature", avgTemperatures);
        result.put("humidity", avgHumidity);
        result.put("pm25", avgPm25);
        return Result.success(result);
    }

    /**
     * 按日期范围计算平均值
     */
    private double[] calculateAverageByDateRange(Long areaId, String startDate, String endDate) {
        // 实际项目中应从数据库查询指定日期范围内的数据并计算平均值
        // 这里使用模拟数据
        Random random = new Random(areaId);
        double temp = 20 + random.nextDouble() * 15; // 20-35度
        double humid = 50 + random.nextDouble() * 30; // 50-80%
        double pm = 30 + random.nextDouble() * 40; // 30-70

        return new double[]{
            Math.round(temp * 100) / 100.0,
            Math.round(humid * 100) / 100.0,
            Math.round(pm * 100) / 100.0
        };
    }

    /**
     * 按周期计算平均值
     */
    private double[] calculateAverageByPeriod(Long areaId, String period) {
        // 实际项目中应从数据库查询指定周期的数据并计算平均值
        // 这里使用模拟数据，不同周期返回不同的模拟值
        Random random = new Random(areaId);
        double baseTemp = 22 + random.nextDouble() * 8;
        double baseHumid = 55 + random.nextDouble() * 20;
        double basePm25 = 35 + random.nextDouble() * 25;

        // 根据周期类型调整
        if ("year".equals(period)) {
            // 年度平均相对稳定
            baseTemp = 24 + random.nextDouble() * 4;
            baseHumid = 60 + random.nextDouble() * 10;
            basePm25 = 40 + random.nextDouble() * 15;
        } else if ("quarter".equals(period)) {
            // 季度平均有变化
            baseTemp = 20 + random.nextDouble() * 12;
            baseHumid = 55 + random.nextDouble() * 20;
            basePm25 = 35 + random.nextDouble() * 25;
        }

        return new double[]{
            Math.round(baseTemp * 100) / 100.0,
            Math.round(baseHumid * 100) / 100.0,
            Math.round(basePm25 * 100) / 100.0
        };
    }
}
