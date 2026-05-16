package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("alert_record")
public class AlertRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long areaId;
    private String factorType;
    private BigDecimal factorValue;
    private String alertLevel;
    private String alertMessage;
    private String suggestion;
    private String status;
    private LocalDateTime createTime;
}
