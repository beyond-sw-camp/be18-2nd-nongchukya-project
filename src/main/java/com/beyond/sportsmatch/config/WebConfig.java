package com.beyond.sportsmatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 파일 시스템의 uploads 폴더를 /uploads/** 경로로 노출
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/") // 프로젝트 루트의 uploads 폴더
                .setCachePeriod(3600) // 캐시 시간 (초)
                .resourceChain(true);
    }
}
