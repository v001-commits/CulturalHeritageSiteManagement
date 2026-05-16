package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("environment_data_realtime")
public class EnvironmentDataRealtime {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long areaId;
    private Long deviceId;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal pm25;
    private BigDecimal so2;
    private BigDecimal no2;
    private BigDecimal windSpeed;
    private String windDirection;
    private BigDecimal lightIntensity;
    private BigDecimal soilMoisture;
    private String waterQuality;
    private String riskLevel;
    private LocalDateTime recordTime;
    private LocalDateTime updateTime;
}
