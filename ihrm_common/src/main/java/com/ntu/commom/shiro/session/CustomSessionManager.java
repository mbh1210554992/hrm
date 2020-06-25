package com.ntu.commom.shiro.session;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 自定义sessionManager
 */
public class CustomSessionManager extends DefaultWebSessionManager {
    /**
     * 头信息中具有sessionId
     *      请求头：Authorization： sessionId
     *
     * 制定sessionId的获取方法
     *
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = WebUtils.toHttp(request).getHeader("Authorization");
        if(StringUtils.isEmpty(id)){
            //如果没有携带，生成新的sessionId
            return super.getSessionId(request, response);
        } else{

            //请求头信息 ： Bearer sessionid
            id = id.replaceAll("Bearer ","");

            //返回sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "cookie");
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        }
    }
}
