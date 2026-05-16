package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("monitor_device")
public class MonitorDevice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long areaId;
    private String deviceName;
    private String deviceType;
    private String status;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private java.math.BigDecimal altitude;
    private String photoUrl;
    private String riskLevel;
    private LocalDateTime lastDataTime;
    private LocalDateTime createTime;
}
