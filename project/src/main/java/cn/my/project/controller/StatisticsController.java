package cn.my.project.controller;

import cn.my.project.common.Result;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/statistics")
@CrossOrigin
public class StatisticsController {

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

    @GetMapping("/compare")
    public Result<Map<String, Object>> getAreaCompare() {
        Map<String, Object> data = new HashMap<>();
        data.put("areas", Arrays.asList("故宫", "长城", "兵马俑"));
        data.put("temperature", Arrays.asList(25.0, 22.0, 28.0));
        data.put("humidity", Arrays.asList(65.0, 55.0, 70.0));
        data.put("pm25", Arrays.asList(45.0, 38.0, 52.0));
        return Result.success(data);
    }
}
