package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.FileInfo;
import cn.my.project.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
                "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt",  // 文档格式
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
}