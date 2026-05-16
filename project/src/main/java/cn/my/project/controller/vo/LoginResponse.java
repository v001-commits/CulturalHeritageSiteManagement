package cn.my.project.controller.vo;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UserInfo user;

    public LoginResponse(String token, UserInfo user) {
        this.token = token;
        this.user = user;
    }

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String name;
        private String role;
        private String email;
        private String phone;

        public UserInfo(Long id, String username, String name, String role, String email, String phone) {
            this.id = id;
            this.username = username;
            this.name = name;
            this.role = role;
            this.email = email;
            this.phone = phone;
        }
    }
}