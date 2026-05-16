package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_area_permission")
public class UserAreaPermission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long areaId;
    private String permissionType;
    private LocalDateTime createTime;
}
