package cn.my.project.controller;

import cn.my.project.common.Result;
import cn.my.project.entity.User;
import cn.my.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");

    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public Result<?> save(@RequestBody User user) {
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            return Result.error("密码必须包含字母、数字、特殊符号，且长度至少8位");
        }
        return userService.save(user) ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping
    public boolean update(@RequestBody User user) {
        return userService.updateById(user);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return userService.removeById(id);
    }
}
