package com.bsingh.doto.Task.Manager.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void  addCorsMappings(CorsRegistry registry){
        registry.addMapping("") // Frontend url
                .allowedOrigins("")
                .allowedMethods("")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
