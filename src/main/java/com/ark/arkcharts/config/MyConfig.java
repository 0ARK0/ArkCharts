package com.ark.arkcharts.config;

import com.ark.arkcharts.config.Interceptor.LoginInterceptor;
import com.ark.arkcharts.myutils.PathUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/", "/admin/login", "/user/login", "/user/register",
                        "/toLogin", "/toRegister", "/static/**", "/usersDir/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射
        registry.addResourceHandler("/usersDir/**")
                .addResourceLocations("file:" + PathUtil.getStartPath() + "/");
    }
}
