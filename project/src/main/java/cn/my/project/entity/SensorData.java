package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sensor_data")
public class SensorData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long areaId;
    private String dataSource;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal precipitation;
    private BigDecimal windSpeed;
    private String windDirection;
    private BigDecimal lightIntensity;
    private BigDecimal pm25;
    private BigDecimal so2;
    private BigDecimal no2;
    private BigDecimal soilMoisture;
    private BigDecimal soilPh;
    private String waterQuality;
    private BigDecimal crackWidth;
    private String weatheringDegree;
    private LocalDateTime recordTime;
    private LocalDateTime createTime;
}
