package com.ntu.domain.system.response;

import com.ntu.domain.system.Permission;
import com.ntu.domain.system.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RoleResult implements Serializable {
    private static final long serialVersionUID = 5541872776082556634L;
    @Id
    private String id;
    /**
     * 角色名
     */
    private String name;
    /**
     * 说明
     */
    private String description;
    /**
     * 企业id
     */
    private String companyId;

    List<String> permIds = new ArrayList<>();

    public RoleResult(Role role){
        BeanUtils.copyProperties(role,this);
        for(Permission permission : role.getPermissions()){
            permIds.add(permission.getId());
        }
    }

}
