package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("threshold_template")
public class ThresholdTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String templateName;
    private String heritageType;
    private String factorType;
    private BigDecimal thresholdMin;
    private BigDecimal thresholdMax;
    private String description;
}
