package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("alert_rule")
public class AlertRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long areaId;
    private String factorType;
    private BigDecimal thresholdMin;
    private BigDecimal thresholdMax;
    private String alertLevel;
    private Integer enable;
    private LocalDateTime createTime;
}
