package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("monitor_area")
public class MonitorArea {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String areaName;
    private String areaCode;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String boundaryPoints;
    private String heritageType;
    private String modelUrl;
    private String cameraPosition;
    private String riskLevel;
    private BigDecimal altitude;
    private LocalDateTime createTime;
}
