package com.ntu.system.service;

import com.ntu.commom.service.BaseService;
import com.ntu.commom.utils.IdWorker;
import com.ntu.commom.utils.PermissionConstants;
import com.ntu.domain.system.Permission;
import com.ntu.domain.system.Role;
import com.ntu.system.dao.PermissionDao;
import com.ntu.system.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService extends BaseService {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private PermissionDao permissionDao;

    /**
     * 新增角色
     */
    public void save(Role role){
        role.setId(idWorker.nextId()+"");
        roleDao.save(role);
    }

    /**
     * 更新角色
     */
    public void update(Role role){
        Role temp = roleDao.findById(role.getId()).get();
        temp.setDescription(role.getDescription());
        temp.setName(role.getName());
        roleDao.save(temp);
    }

    public Role findById(String id){
        return roleDao.findById(id).get();
    }

    public List<Role> findAll(String companyId){

        return roleDao.findAll(getSpec(companyId));
    }

    public void deleteById(String id){
        roleDao.deleteById(id);
    }

    public Page<Role> findByPage(String companyId, int page, int size){
        Pageable pageable = PageRequest.of(page-1, size);
        return roleDao.findAll(getSpec(companyId),pageable);
    }
    /**
     * 分配权限
     *
     */
    public  void assignPerms(String roleId, List<String> permIds){
        //1.获取分配的角色对象
        Role role = roleDao.findById(roleId).get();
        //2.构造角色权限集合
        Set<Permission> perms = new HashSet<>();
        for(String perm : permIds){
            Permission permission = permissionDao.findById(perm).get();
            //根据父id和类型查询API权限列表
            List<Permission> apiList = permissionDao.findByTypeAndPid(PermissionConstants.PY_API,permission.getId());
            perms.addAll(apiList);//自动赋予api权限
            perms.add(permission);//当前的菜单或按钮
        }
        role.setPermissions(perms);
        roleDao.save(role);
    }

}
