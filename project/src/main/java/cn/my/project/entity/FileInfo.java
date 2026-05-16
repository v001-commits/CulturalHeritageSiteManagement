package cn.my.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_info")
public class FileInfo {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String fileName;
    
    private String originalName;
    
    private String fileType;
    
    private Long fileSize;
    
    private String filePath;
    
    private String fileUrl;
    
    private String module;
    
    private Long userId;
    
    private String userName;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}