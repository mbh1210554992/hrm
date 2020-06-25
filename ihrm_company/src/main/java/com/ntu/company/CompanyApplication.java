package com.ntu.company;

import com.ntu.commom.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//1.配置spring boot的包扫描
//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class, scanBasePackages = "com.ntu.company")
//2.配置JPA注解扫描
@EntityScan(value = "com.ntu.domain.company")
@EnableJpaRepositories(basePackages = "com.ntu.company.dao")
@SpringBootApplication
@EnableEurekaClient
public class CompanyApplication {
    /**
     * 启动方法
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(CompanyApplication.class,args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker();
    }
}
