package com.ai.AVAI.Service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Разрешите только ваш фронтенд
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*") // Разрешите все заголовки
                .allowCredentials(true); // Если используются cookies или авторизация
    }
}
