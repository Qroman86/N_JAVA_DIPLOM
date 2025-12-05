package ru.netology.cloudstorage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/cloud/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:8081")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}
