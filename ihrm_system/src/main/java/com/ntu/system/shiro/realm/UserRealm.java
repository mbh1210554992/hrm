package com.ntu.system.shiro.realm;

import com.ntu.commom.shiro.realm.IhrmRealm;
import com.ntu.domain.system.Permission;
import com.ntu.domain.system.User;
import com.ntu.domain.system.response.ProfileResult;
import com.ntu.system.service.PermissionService;
import com.ntu.system.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRealm extends IhrmRealm {
    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    //登陆认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1.获取用户的手机号的密码
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;
        String mobile = upToken.getUsername();
        String password = new String(upToken.getPassword());
        //2.根据手机号查询用户
        User user = userService.findByMobile(mobile);
        //3.判断账户是否正确

        if(user !=null && password.equals(user.getPassword())){
            ProfileResult result = null;
            Map map = new HashMap();
            if("user".equals(user.getLevel())){
                result = new ProfileResult(user);
            }
            else if("saasAdmin".equals(user.getLevel())){

                //1.saas平台管理员具有所有权限
                List<Permission> permissions = permissionService.findAll(map);
                result = new ProfileResult(user, permissions);
            }
            else{

                //2.企业管理员具有所有的企业权限
                map.put("enVisible",1);
                List<Permission> permissions = permissionService.findAll(map);
                result = new ProfileResult(user, permissions);
            }

            //4.构造安全数据并返回（安全数据：用户基本信息，用户权限）
            //构造方法（安全数据，密码，realm域名）
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(result,password,this.getName());
            return info;
        }

        //会抛出异常，表示用户名和密码不匹配
        return null;
    }

    public static void main(String[] args) {
        String s = new Md5Hash("123456","13800000002",3).toString();
        System.out.println(s);
    }
}
