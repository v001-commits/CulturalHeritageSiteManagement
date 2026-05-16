package cn.my.project.controller;

import cn.my.project.common.Result;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Random;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    /**
     * 生成图形验证码（4位随机数字）
     */
    @GetMapping("/generate")
    public Result<?> generateCaptcha(HttpSession session) {
        // 生成4位随机数字验证码
        String captcha = String.format("%04d", new Random().nextInt(9000) + 1000);
        
        // 保存到session中
        session.setAttribute("captcha", captcha);
        
        // 返回验证码
        return Result.success(captcha);
    }

    /**
     * 验证图形验证码
     */
    @PostMapping("/verify")
    public Result<?> verifyCaptcha(@RequestBody VerifyRequest req, HttpSession session) {
        String sessionCaptcha = (String) session.getAttribute("captcha");
        
        if (sessionCaptcha == null) {
            return Result.error("验证码已过期，请刷新");
        }
        
        if (!sessionCaptcha.equals(req.getCaptcha())) {
            return Result.error("图形验证码错误");
        }
        
        return Result.success("验证成功");
    }

    @Data
    static class VerifyRequest {
        private String captcha;
    }
}
