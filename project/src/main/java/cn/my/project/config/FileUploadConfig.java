package cn.my.project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    /**
     * 文件上传基础路径
     */
    private String basePath = "uploads";

    /**
     * 允许的文件类型
     */
    private String[] allowedTypes = {
        "jpg", "jpeg", "png", "gif", "bmp", "webp",  // 图片格式
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt",  // 文档格式
        "mp4", "avi", "mov", "wmv", "flv", "mkv",  // 视频格式
        "mp3", "wav", "flac", "aac", "ogg",  // 音频格式
        "zip", "rar", "7z", "tar", "gz"  // 压缩格式
    };

    /**
     * 最大文件大小（字节）默认100MB
     */
    private long maxSize = 100 * 1024 * 1024;

    /**
     * 访问URL前缀
     */
    private String urlPrefix = "/files";

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String[] getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String[] allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    /**
     * 检查文件类型是否允许
     */
    public boolean isAllowedType(String fileType) {
        if (fileType == null || fileType.isEmpty()) {
            return false;
        }
        
        String lowerFileType = fileType.toLowerCase();
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(lowerFileType)) {
                return true;
            }
        }
        return false;
    }
}