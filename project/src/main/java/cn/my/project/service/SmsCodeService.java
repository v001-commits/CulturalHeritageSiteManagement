package cn.my.project.service;

import cn.my.project.entity.SmsCode;
import cn.my.project.mapper.SmsCodeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SmsCodeService extends ServiceImpl<SmsCodeMapper, SmsCode> {

    public String sendCode(String phone) {
        // 生成4位随机验证码 (1000-9999)
        String code = String.format("%04d", new Random().nextInt(9000) + 1000);
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
        save(smsCode);
        return code;
    }

    public boolean verifyCode(String phone, String code) {
        SmsCode smsCode = getOne(new LambdaQueryWrapper<SmsCode>()
                .eq(SmsCode::getPhone, phone)
                .eq(SmsCode::getCode, code)
                .gt(SmsCode::getExpireTime, LocalDateTime.now())
                .orderByDesc(SmsCode::getCreateTime)
                .last("limit 1"));
        return smsCode != null;
    }
}
