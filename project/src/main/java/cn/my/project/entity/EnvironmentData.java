package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("environment_data")
public class EnvironmentData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long areaId;
    private String riskLevel;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal pm25;
    private LocalDateTime createTime;
}
