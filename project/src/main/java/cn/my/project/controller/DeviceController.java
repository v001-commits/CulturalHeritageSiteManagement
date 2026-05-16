package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.MonitorDevice;
import cn.my.project.entity.FileInfo;
import cn.my.project.service.MonitorDeviceService;
import cn.my.project.service.FileUploadService;
import cn.my.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private MonitorDeviceService deviceService;

    @Autowired
    private FileUploadService fileUploadService;

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    private boolean isAdmin(HttpServletRequest request) {
        String token = extractToken(request);
        String role = JwtUtil.getRole(token);
        return "admin".equals(role);
    }

    /**
     * 获取设备列表（仅管理员）
     */
    @GetMapping("/list")
    public Result<List<MonitorDevice>> list(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error("无权限访问");
        }
        return Result.success(deviceService.list());
    }

    /**
     * 根据ID获取设备详情
     */
    @GetMapping("/{id}")
    public Result<MonitorDevice> getById(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error("无权限访问");
        }
        return Result.success(deviceService.getById(id));
    }

    /**
     * 添加设备
     */
    @PostMapping
    public Result<?> add(@RequestBody MonitorDevice device, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error("无权限操作");
        }
        device.setCreateTime(LocalDateTime.now());
        return deviceService.save(device) ?
                Result.success("添加设备成功") : Result.error("添加设备失败");
    }

    /**
     * 更新设备信息
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody MonitorDevice device, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error("无权限操作");
        }
        device.setId(id);
        return deviceService.updateById(device) ?
                Result.success("更新设备成功") : Result.error("更新设备失败");
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error("无权限操作");
        }
        return deviceService.removeById(id) ?
                Result.success("删除设备成功") : Result.error("删除设备失败");
    }

    /**
     * 上传设备照片
     * @param file 照片文件
     * @param deviceId 设备ID（可选，如果提供则自动更新设备照片URL）
     */
    @PostMapping("/upload-photo")
    public Result<String> uploadPhoto(@RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "deviceId", required = false) Long deviceId,
                                       HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error("无权限操作");
        }
        try {
            FileInfo fileInfo = fileUploadService.uploadFile(file, "device", request);
            String photoUrl = fileInfo.getFileUrl();

            // 如果提供了设备ID，自动更新设备的照片URL
            if (deviceId != null) {
                MonitorDevice device = deviceService.getById(deviceId);
                if (device != null) {
                    device.setPhotoUrl(photoUrl);
                    deviceService.updateById(device);
                }
            }
            return Result.success(photoUrl);
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}
