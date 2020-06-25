package com.ntu.domain.system.response;

import com.ntu.domain.system.Permission;
import com.ntu.domain.system.Role;
import com.ntu.domain.system.User;
import lombok.Data;
import org.crazycake.shiro.AuthCachePrincipal;

import java.io.Serializable;
import java.util.*;

@Data
public class ProfileResult implements Serializable, AuthCachePrincipal {
    private String mobile;
    private String username;
    private String company;
    private String companyId;
    private String userId;
    private Map<String, Object> roles = new HashMap<>();


    public ProfileResult(User user, List<Permission> permissions){
        this.mobile = user.getMobile();
        this.company = user.getCompanyName();
        this.username = user.getUsername();
        this.companyId = user.getCompanyId();
        this.userId = user.getId();
        Set<Role> roleSet = user.getRoles();

        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        for(Permission permission : permissions){
            if(permission.getType() == 1){
                menus.add(permission.getCode());
            }else if(permission.getType() == 2){
                points.add(permission.getCode());
            }else {
                apis.add(permission.getCode());
            }
        }
        this.roles.put("menus",menus);
        this.roles.put("points",points);
        this.roles.put("apis",apis);
    }


    public ProfileResult(User user){
        this.mobile = user.getMobile();
        this.company = user.getCompanyName();
        this.username = user.getUsername();
        Set<Role> roleSet = user.getRoles();

        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();

        for(Role role : roleSet){
            Set<Permission> permissions = role.getPermissions();
            for(Permission permission : permissions){
                if(permission.getType() == 1){
                    menus.add(permission.getCode());
                }else if(permission.getType() == 2){
                    points.add(permission.getCode());
                }else {
                    apis.add(permission.getCode());
                }
            }
        }
        this.roles.put("menus",menus);
        this.roles.put("points",points);
        this.roles.put("apis",apis);
    }

    @Override
    public String getAuthCacheKey() {
        return null;
    }
}
