package cn.my.project.config;

import cn.my.project.interceptor.JwtInterceptor;
import cn.my.project.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT认证拦截器
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**", "/captcha/**", "/file/**", "/statistics/**");
        
        // 权限拦截器 - 检查区域访问权限（仅对非管理员）
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/sensor/**", "/monitor/**", "/environment/**", "/data/**")
                .excludePathPatterns("/auth/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        String uploadPath = "file:" + projectRoot + "/uploads/";
        
        // 映射文件访问路径
        registry.addResourceHandler("/files/**")
                .addResourceLocations(uploadPath);
        
        // 映射静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
