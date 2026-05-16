package cn.my.project.service;

import cn.my.project.config.FileUploadConfig;
import cn.my.project.entity.FileInfo;
import cn.my.project.mapper.FileInfoMapper;
import cn.my.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Value("${user.dir}")
    private String userDir;

    /**
     * 单文件上传
     */
    @Transactional
    public FileInfo uploadFile(MultipartFile file, String module, HttpServletRequest request) throws IOException {
        // 验证文件
        validateFile(file);

        // 生成文件信息
        FileInfo fileInfo = createFileInfo(file, module, request);

        // 保存文件到磁盘
        saveFileToDisk(file, fileInfo);

        // 保存文件信息到数据库
        fileInfoMapper.insert(fileInfo);

        return fileInfo;
    }

    /**
     * 多文件上传
     */
    @Transactional
    public List<FileInfo> uploadFiles(MultipartFile[] files, String module, HttpServletRequest request) throws IOException {
        List<FileInfo> fileInfos = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                FileInfo fileInfo = uploadFile(file, module, request);
                fileInfos.add(fileInfo);
            }
        }
        
        return fileInfos;
    }

    /**
     * 获取文件信息列表
     */
    public List<FileInfo> getFileList(String module) {
        return fileInfoMapper.selectByModule(module);
    }

    /**
     * 根据ID获取文件信息
     */
    public FileInfo getFileInfo(Long id) {
        return fileInfoMapper.selectById(id);
    }

    /**
     * 根据用户ID获取文件列表
     */
    public List<FileInfo> getFileListByUserId(Long userId) {
        return fileInfoMapper.selectByUserId(userId);
    }

    /**
     * 删除文件
     */
    @Transactional
    public boolean deleteFile(Long id) {
        FileInfo fileInfo = fileInfoMapper.selectById(id);
        if (fileInfo == null) {
            return false;
        }

        // 删除磁盘文件
        File file = new File(fileInfo.getFilePath());
        if (file.exists()) {
            file.delete();
        }

        // 删除数据库记录
        return fileInfoMapper.deleteById(id) > 0;
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > fileUploadConfig.getMaxSize()) {
            throw new IllegalArgumentException("文件大小超过限制：" + 
                formatFileSize(fileUploadConfig.getMaxSize()));
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String fileExtension = getFileExtension(originalFilename);
            if (!fileUploadConfig.isAllowedType(fileExtension)) {
                throw new IllegalArgumentException("不支持的文件类型：" + fileExtension);
            }
        }

        // 检查文件内容
        byte[] bytes = file.getBytes();
        if (bytes.length == 0) {
            throw new IllegalArgumentException("文件内容为空");
        }
    }

    /**
     * 创建文件信息对象
     */
    private FileInfo createFileInfo(MultipartFile file, String module, HttpServletRequest request) {
        FileInfo fileInfo = new FileInfo();
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + fileExtension;
        
        fileInfo.setOriginalName(originalFilename);
        fileInfo.setFileName(newFileName);
        fileInfo.setFileType(fileExtension.toLowerCase());
        fileInfo.setFileSize(file.getSize());
        fileInfo.setModule(module);
        fileInfo.setCreateTime(LocalDateTime.now());
        fileInfo.setUpdateTime(LocalDateTime.now());

        // 设置用户信息（如果能从token中获取）
        try {
            String token = getTokenFromRequest(request);
            if (StringUtils.hasText(token)) {
                Long userId = JwtUtil.getUserId(token);
                String username = JwtUtil.getUsername(token);
                fileInfo.setUserId(userId);
                fileInfo.setUserName(username);
            }
        } catch (Exception e) {
            // 如果无法解析token，用户信息留空
        }

        // 设置文件路径和访问URL
        String datePath = getDatePath();
        String relativePath = datePath + "/" + newFileName;
        String absolutePath = fileUploadConfig.getBasePath() + "/" + relativePath;
        
        // 存储相对路径用于数据库
        fileInfo.setFilePath(absolutePath);
        fileInfo.setFileUrl(fileUploadConfig.getUrlPrefix() + "/" + relativePath);

        return fileInfo;
    }

    /**
     * 保存文件到磁盘
     */
    private void saveFileToDisk(MultipartFile file, FileInfo fileInfo) throws IOException {
        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        // 构建完整路径
        String fullPath = projectRoot + File.separator + fileInfo.getFilePath();
        File destFile = new File(fullPath);
        
        // 创建目录
        File parentDir = destFile.getParentFile();
        if (!parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                throw new IOException("无法创建目录: " + parentDir.getAbsolutePath());
            }
        }
        
        // 保存文件
        file.transferTo(destFile);
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 获取日期路径（年/月/日）
     */
    private String getDatePath() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%d/%02d/%02d", year, month, day);
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2fKB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2fGB", size / (1024.0 * 1024 * 1024));
        }
    }
}