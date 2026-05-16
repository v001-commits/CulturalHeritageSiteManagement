package cn.my.project.service;

import cn.my.project.entity.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.my.project.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
}
