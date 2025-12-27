package com.example.onlinebookstorebackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器配置
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/user/public",
                        "/api/books/search"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 跨域配置（开发环境）
        registry.addMapping("/api/**")
                .allowedOrigins("*")  // 开发环境允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)  // 如果要传cookie，设置为true
                .maxAge(3600);
    }
}