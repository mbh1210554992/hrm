package com.ntu.commom.interceptor;

import com.ntu.commom.entity.ResultCode;
import com.ntu.commom.exception.CommonException;
import com.ntu.commom.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 *  preHandle：进入控制器方法之前执行
 *      boolean: true: 可以继续执行控制器方法。 false：拦截
 *  post....:执行控制器方法之后的内容
 *  afterCompletion：响应结束之前执行的内容
 *
 *
 *
 *  1.简化获取token数据的代码编写
 *      统一的用户权限校验（是否登陆）
 *  2.判断用户是否具有当前访问接口的权限
 */
@Component
public class JwtInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 1.通过拦截器获取token数据
         */

        //1.通过request获取到token信息
        String authorization = request.getHeader("Authorization");
        //判断请求头是否为空或是否已Bearer开头
        if(!StringUtils.isEmpty(authorization) && authorization.startsWith("Bearer")){
            //获取token
            String token = authorization.replace("Bearer ","");
            Claims claims = jwtUtils.parseJwt(token);
            if(claims != null){
                //通过claims获取到当前用户的可访问的api权限
                String apis = (String) claims.get("apis");
                //通过handler
                HandlerMethod h = (HandlerMethod) handler;
                RequestMapping annotation = h.getMethodAnnotation(RequestMapping.class);
                //获取当前接口中的name属性
                String name = annotation.name();
                if(apis.contains(name)){
                    request.setAttribute("user_claims",claims);
                    return true;
                }else{
                    throw new CommonException(ResultCode.UNAUTHORISE);
                }


            }
        }
        throw new CommonException(ResultCode.UNAUTHENTICATED);

    }

}
