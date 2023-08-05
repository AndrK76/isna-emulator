package ru.igorit.andrk.config.api;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Log4j2
//@ConditionalOnProperty(prefix = "api", name = "cors-allow-any", havingValue = "true")
public class CorsConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/api/**")
                .allowedMethods("*")
                .allowedOrigins("*");
    }
}
