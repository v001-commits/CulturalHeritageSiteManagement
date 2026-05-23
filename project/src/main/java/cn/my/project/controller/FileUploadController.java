package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.FileInfo;
import cn.my.project.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 单文件上传
     * @param file 文件
     * @param module 模块标识（可选）
     * @return 文件信息
     */
    @PostMapping("/upload")
    public Result<FileInfo> upload(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "module", defaultValue = "default") String module,
                                   HttpServletRequest request) {
        try {
            FileInfo fileInfo = fileUploadService.uploadFile(file, module, request);
            return Result.success(fileInfo);
        } catch (Exception e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 多文件上传
     * @param files 文件数组
     * @param module 模块标识（可选）
     * @return 文件信息列表
     */
    @PostMapping("/uploads")
    public Result<List<FileInfo>> uploads(@RequestParam("files") MultipartFile[] files,
                                          @RequestParam(value = "module", defaultValue = "default") String module,
                                          HttpServletRequest request) {
        try {
            List<FileInfo> fileInfos = fileUploadService.uploadFiles(files, module, request);
            return Result.success(fileInfos);
        } catch (Exception e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定模块的文件列表
     * @param module 模块标识
     * @return 文件信息列表
     */
    @GetMapping("/list")
    public Result<List<FileInfo>> getFileList(@RequestParam(value = "module", required = false) String module) {
        try {
            List<FileInfo> fileInfos = fileUploadService.getFileList(module);
            return Result.success(fileInfos);
        } catch (Exception e) {
            return Result.error("获取文件列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取文件信息
     * @param id 文件ID
     * @return 文件信息
     */
    @GetMapping("/{id}")
    public Result<FileInfo> getFileInfo(@PathVariable Long id) {
        try {
            FileInfo fileInfo = fileUploadService.getFileInfo(id);
            if (fileInfo != null) {
                return Result.success(fileInfo);
            } else {
                return Result.error("文件不存在");
            }
        } catch (Exception e) {
            return Result.error("获取文件信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取文件列表
     * @param userId 用户ID
     * @return 文件信息列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<FileInfo>> getFileListByUserId(@PathVariable Long userId) {
        try {
            List<FileInfo> fileInfos = fileUploadService.getFileListByUserId(userId);
            return Result.success(fileInfos);
        } catch (Exception e) {
            return Result.error("获取用户文件列表失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param id 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteFile(@PathVariable Long id) {
        try {
            boolean result = fileUploadService.deleteFile(id);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("文件删除失败");
            }
        } catch (Exception e) {
            return Result.error("文件删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取系统支持的文件类型
     * @return 支持的文件类型列表
     */
    @GetMapping("/types")
    public Result<String[]> getSupportedTypes() {
        try {
            // 这里可以注入FileUploadConfig来获取配置
            String[] types = {
                "jpg", "jpeg", "png", "gif", "bmp", "webp",  // 图片格式
                "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv",  // 文档格式
                "mp4", "avi", "mov", "wmv", "flv", "mkv",  // 视频格式
                "mp3", "wav", "flac", "aac", "ogg",  // 音频格式
                "zip", "rar", "7z", "tar", "gz"  // 压缩格式
            };
            return Result.success(types);
        } catch (Exception e) {
            return Result.error("获取支持类型失败：" + e.getMessage());
        }
    }

    /**
     * 获取上传配置信息
     * @return 配置信息
     */
    @GetMapping("/config")
    public Result<Object> getConfig() {
        try {
            // 返回基本配置信息
            return Result.success(new Object() {
                public final String maxSize = "100MB";
                public final String urlPrefix = "/files";
                public final String basePath = "uploads";
            });
        } catch (Exception e) {
            return Result.error("获取配置信息失败：" + e.getMessage());
        }
    }

    /**
     * 文件预览（在浏览器中打开）
     * @param id 文件ID
     * @return 文件资源
     */
    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> previewFile(@PathVariable Long id) throws IOException {
        FileInfo fileInfo = fileUploadService.getFileInfo(id);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        String projectRoot = System.getProperty("user.dir");
        File file = new File(projectRoot + File.separator + fileInfo.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        String contentType = getContentType(fileInfo.getFileType());

        String encodedFileName = URLEncoder.encode(fileInfo.getOriginalName(), "UTF-8");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }

    /**
     * Office文档公开预览接口（用于微软在线预览服务）
     * 通过临时token验证，返回公开可访问的文件
     * @param id 文件ID
     * @param token 临时访问token
     * @return 文件资源
     */
    @GetMapping("/public/preview/{id}")
    public ResponseEntity<Resource> publicPreviewFile(
            @PathVariable Long id,
            @RequestParam String token) throws IOException {
        // 简单验证token（实际项目中应该使用更安全的方式）
        if (!isValidPreviewToken(id, token)) {
            return ResponseEntity.status(403).build();
        }

        FileInfo fileInfo = fileUploadService.getFileInfo(id);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        String projectRoot = System.getProperty("user.dir");
        File file = new File(projectRoot + File.separator + fileInfo.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        String contentType = getContentType(fileInfo.getFileType());

        String encodedFileName = URLEncoder.encode(fileInfo.getOriginalName(), "UTF-8");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .body(resource);
    }

    /**
     * 生成预览token（简单实现：文件ID + 时间戳的简单签名）
     */
    private String generatePreviewToken(Long fileId) {
        long timestamp = System.currentTimeMillis() / 1000 / 300; // 5分钟有效
        String data = fileId + "_" + timestamp + "_preview_secret";
        return Integer.toHexString(data.hashCode());
    }

    /**
     * 验证预览token
     */
    private boolean isValidPreviewToken(Long fileId, String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String expectedToken = generatePreviewToken(fileId);
        return expectedToken.equals(token);
    }

    /**
     * 文件下载
     * @param id 文件ID
     * @return 文件资源
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        FileInfo fileInfo = fileUploadService.getFileInfo(id);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        String projectRoot = System.getProperty("user.dir");
        File file = new File(projectRoot + File.separator + fileInfo.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        String contentType = getContentType(fileInfo.getFileType());

        String encodedFileName = URLEncoder.encode(fileInfo.getOriginalName(), "UTF-8");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }

    /**
     * 根据文件扩展名获取ContentType
     */
    private String getContentType(String fileType) {
        if (fileType == null) {
            return "application/octet-stream";
        }

        switch (fileType.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            case "pdf":
                return "application/pdf";
            case "txt":
            case "csv":
                return "text/plain";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            default:
                return "application/octet-stream";
        }
    }
}