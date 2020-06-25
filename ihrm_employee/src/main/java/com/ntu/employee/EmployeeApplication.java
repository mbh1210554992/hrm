package com.ntu.employee;

import com.ntu.commom.utils.IdWorker;
import com.ntu.commom.utils.JwtUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = "com.ntu")
@EntityScan("com.ntu.domain.employee")
public class EmployeeApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        configurer.favorPathExtension(false);

    }
    @Bean
    public IdWorker idWorkker() {
        return new IdWorker(1, 1);
    }

    @Bean
    public JwtUtils jwtUtil() {
        return new JwtUtils();
    }
}