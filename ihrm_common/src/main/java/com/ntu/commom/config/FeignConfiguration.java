package com.ntu.commom.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Configuration
public class FeignConfiguration {
    //配置fegin拦截器， 解决请求头问题
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            //获取所有浏览器发送的请求属性，请求头赋值到feign
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //请求属性
                ServletRequestAttributes requestAttributes =
                        (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                if(requestAttributes != null){
                    HttpServletRequest request = requestAttributes.getRequest();
                   //获取浏览器发起的请求头
                    Enumeration<String> headerNames = request.getHeaderNames();
                    if(headerNames != null){
                        while(headerNames.hasMoreElements()){
                            String name = headerNames.nextElement();    //请求头名称
                            String value = request.getHeader(name);     //请求头数据
                            requestTemplate.header(name,value);

                        }
                    }
                }
            }
        };
    }
}
