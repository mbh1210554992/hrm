package com.ntu.commom.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;
import java.util.Map;

@Data
@ConfigurationProperties("jwt.config")
public class JwtUtils {
    //签名的私钥
    private String key;
    //签名的失效时间
    private Long ttl;

    /**
     * 设置认证token
     *    id:登陆用户的id
     *    subject：登陆用户名
     *    map:其他的私有数据
     */
    public String createJwt(String id, String name, Map<String,Object> map){
        //1.设置失效时间
        long now =System.currentTimeMillis();
        long exp = now + ttl;
        //2.创建token
        JwtBuilder jwtBuilder = Jwts.builder().setId(id).setSubject(name)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256,key);//加密方法
        //根据map设置claims（私有数据）
        for(Map.Entry<String,Object> entry : map.entrySet()){
            jwtBuilder.claim(entry.getKey(),entry.getValue());
        }
        //注入失效时间
        jwtBuilder.setExpiration(new Date(exp));
        String token = jwtBuilder.compact();
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        claims.getId();
        return token;
    }

    /**
     * 解析token获取Claims
     */
    public Claims parseJwt(String token){
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return claims;
    }
}
