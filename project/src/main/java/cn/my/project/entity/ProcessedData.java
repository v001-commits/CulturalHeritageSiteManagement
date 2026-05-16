package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("processed_data")
public class ProcessedData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sourceId;
    private Long areaId;
    private Long zoneId;
    private String dataSource;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal pm25;
    private Integer isAnomaly;
    private String anomalyReason;
    private String processStatus;
    private Integer version;
    private LocalDateTime recordTime;
    private LocalDateTime processTime;
    private LocalDateTime createTime;
}
