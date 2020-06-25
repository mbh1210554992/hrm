package com.ntu.company.config;

import com.ntu.commom.shiro.realm.IhrmRealm;
import com.ntu.commom.shiro.session.CustomSessionManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    /*@Autowired
    private CustomRealm realm;*/

    //1.创建realm
    @Bean
    public IhrmRealm getRealm(){
        return new IhrmRealm();
    }
    //2.创建安全管理器
    @Bean
    public SecurityManager getSecurityManager(IhrmRealm realm){
        //配置realm域
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm);

        //将自定义的session管理器注册到安全管理器中
        securityManager.setSessionManager(sessionManager());
        //将自定义的redis缓存管理器注册
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }

    //3.配置shiro的过滤器工厂

    /**
     * 在web程序中，shiro进行权限控制全部是通过一组过滤器集合进行控制
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
        //创建过滤器工厂
        ShiroFilterFactoryBean filterFactory = new ShiroFilterFactoryBean();
        //设置安全管理器
        filterFactory.setSecurityManager(securityManager);
        //通用配置（跳转登陆界面，为授权跳转的页面）
        filterFactory.setLoginUrl("/autherror?code=1");//未登录时跳转的页面
        filterFactory.setUnauthorizedUrl("/autherror?code=2"); //访问未授权的页面
        //设置过滤器集合

        /**
         * 设置所有的锅过滤器 ：有顺序的map集合
         *      key=拦截的url地址
         *      value = 过滤器类型
         */
        Map<String,String> filterMap = new LinkedHashMap<>();
        filterMap.put("/sys/login","anon");//当前请求地址可以匿名访问
        filterMap.put("/autherror","anon");
        filterMap.put("/user/home","perms[user-home]"); //具备user-home权限才会通过；若不具备，会跳转到setUnauthorizedUrl的地址
        filterMap.put("/**","authc"); //当前请求地址必须认证后才能访问

        filterFactory.setFilterChainDefinitionMap(filterMap);

        return filterFactory;
    }

    //
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    /**
     * 1.redis的控制器，操作redis
     *
     * @return
     */
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        return redisManager;
    }


    /**
     *
     * 2.sessionDao
     * @return
     */
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setRedisManager(redisManager());

        return sessionDAO;
    }

    /**
     * 3.会话管理器
     * @return
     */
    public DefaultWebSessionManager sessionManager(){
        CustomSessionManager sessionManager = new CustomSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        //禁用cookie
        sessionManager.setSessionIdCookieEnabled(false);
        //禁止url重写    url:jsessionid = id
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;

    }

    /**
     * 4.缓存管理器
     * @return
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }



    //4.配置shiro注解支持
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
