package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.User;
import cn.my.project.entity.OperationLog;
import cn.my.project.service.UserService;
import cn.my.project.service.SmsCodeService;
import cn.my.project.service.OperationLogService;
import cn.my.project.utils.JwtUtil;
import cn.my.project.controller.vo.LoginResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OperationLogService operationLogService;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest req) {
        // 支持用户名或手机号登录
        User user = null;
        
        // 先尝试用户名查询
        user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        
        // 如果用户名查询失败，尝试手机号查询（仅限监测人员）
        if (user == null) {
            user = userService.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, req.getUsername())
                    .eq(User::getRole, "monitor")); // 只有监测人员可以用手机号登录
        }

        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        if (user.getEnable() == 0) {
            return Result.error("账号已被禁用");
        }

        if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
            return Result.error("账号已锁定，请30分钟后再试");
        }

        if (!user.getPassword().equals(req.getPassword())) {
            int failCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
            user.setLoginFailCount(failCount);
            if (failCount >= 5) {
                user.setLockTime(LocalDateTime.now().plusMinutes(30));
                userService.updateById(user);
                return Result.error("连续输错5次，账号已锁定30分钟");
            }
            userService.updateById(user);
            return Result.error("用户名或密码错误，还剩" + (5 - failCount) + "次机会");
        }

        user.setLoginFailCount(0);
        user.setLockTime(null);
        userService.updateById(user);

        String token = JwtUtil.generate(user.getId(), user.getUsername(), user.getRole());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(user.getId(), user.getUsername(), user.getName(), user.getRole(), user.getEmail(), user.getPhone());
        
        // 记录登录日志
        logOperation(user.getId(), user.getUsername(), "用户登录", "登录成功", "success");
        
        return Result.success(new LoginResponse(token, userInfo));
    }

    @PostMapping("/phone-login")
    public Result<?> phoneLogin(@RequestBody PhoneLoginRequest req, HttpSession session) {
        // 1. 验证图形验证码
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (sessionCaptcha == null) {
            return Result.error("图形验证码已过期，请刷新");
        }
        
        if (req.getCaptcha() == null || !sessionCaptcha.equals(req.getCaptcha())) {
            return Result.error("图形验证码错误");
        }
        
        // 2. 验证手机号是否属于监测人员
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, req.getPhone())
                .eq(User::getRole, "monitor"));
        
        if (user == null) {
            return Result.error("手机号未注册或不是监测人员账号");
        }

        if (user.getEnable() == 0) {
            return Result.error("账号已被禁用");
        }

        // 3. 清除已使用的图形验证码
        session.removeAttribute("captcha");

        // 4. 生成token并返回
        String token = JwtUtil.generate(user.getId(), user.getUsername(), user.getRole());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(user.getId(), user.getUsername(), user.getName(), user.getRole(), user.getEmail(), user.getPhone());
        
        // 记录登录日志
        logOperation(user.getId(), user.getUsername(), "用户登录", "手机号+图形验证码登录成功", "success");
        
        return Result.success(new LoginResponse(token, userInfo));
    }

    /**
     * 检查用户是否可以使用手机登录
     */
    @GetMapping("/check-phone-login")
    public Result<?> checkPhoneLogin(@RequestParam String identifier) {
        // 检查是否是监测人员（通过用户名或手机号）
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .and(wrapper -> wrapper
                        .eq(User::getUsername, identifier)
                        .or()
                        .eq(User::getPhone, identifier))
                .eq(User::getRole, "monitor"));
        
        boolean canUsePhoneLogin = user != null;
        return Result.success(canUsePhoneLogin);
    }

    @PostMapping("/validate-password")
    public Result<?> validatePassword(@RequestBody ValidatePasswordRequest req) {
        if (!PASSWORD_PATTERN.matcher(req.getPassword()).matches()) {
            return Result.error("密码必须包含字母、数字、特殊符号，且长度至少8位");
        }
        return Result.success("密码符合要求");
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    static class PhoneLoginRequest {
        private String phone;
        private String captcha;  // 图形验证码
    }

    @Data
    static class ValidatePasswordRequest {
        private String password;
    }

    /**
     * 记录操作日志
     */
    private void logOperation(Long userId, String username, String operationType, String operationContent, String operationResult) {
        try {
            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setOperationType(operationType);
            log.setOperationContent(operationContent);
            log.setOperationResult(operationResult);
            log.setIpAddress("127.0.0.1");
            log.setCreateTime(LocalDateTime.now());
            operationLogService.save(log);
        } catch (Exception e) {
            // 日志记录失败不影响主流程
        }
    }
}
