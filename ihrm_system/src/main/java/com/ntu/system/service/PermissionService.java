package com.ntu.system.service;

import com.ntu.commom.entity.ResultCode;
import com.ntu.commom.exception.CommonException;
import com.ntu.commom.utils.BeanMapUtils;
import com.ntu.commom.utils.IdWorker;
import com.ntu.commom.utils.PermissionConstants;
import com.ntu.domain.system.Permission;
import com.ntu.domain.system.PermissionApi;
import com.ntu.domain.system.PermissionMenu;
import com.ntu.domain.system.PermissionPoint;
import com.ntu.system.dao.PermissionApiDao;
import com.ntu.system.dao.PermissionDao;
import com.ntu.system.dao.PermissionMenuDao;
import com.ntu.system.dao.PermissionPointDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ntu.commom.utils.BeanMapUtils.mapToBean;

@Service
public class PermissionService {
    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private PermissionMenuDao permissionMenuDao;

    @Autowired
    private PermissionApiDao permissionApiDao;

    @Autowired
    private PermissionPointDao permissionPointDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 保存用户
     */
    public void add(Map<String,Object> map) throws Exception {
        //主键设置
        String id = idWorker.nextId()+"";
        //1.通过map构造permission对象
        Permission permission = BeanMapUtils.mapToBean(map,Permission.class);
        permission.setId(id);
        //2.根据类型构造不同的资源对象（菜单，按钮， api）
        int type = permission.getType();
        switch(type){
            case PermissionConstants.PY_MENU:
                PermissionMenu permissionMenu = mapToBean(map,PermissionMenu.class);
                permissionMenu.setId(id);
                permissionMenuDao.save(permissionMenu);
                break;
            case PermissionConstants.PY_POINT:
                PermissionPoint permissionPoint = mapToBean(map,PermissionPoint.class);
                permissionPoint.setId(id);
                permissionPointDao.save(permissionPoint);
                break;
            case PermissionConstants.PY_API:
                PermissionApi permissionApi = mapToBean(map,PermissionApi.class);
                permissionApi.setId(id);
                permissionApiDao.save(permissionApi);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);

        }
        //3.保存
        permissionDao.save(permission);


    }

    /**
     * 更新权限
     * @param map
     * @throws Exception
     */
    public void update(Map<String, Object> map) throws Exception {
        Permission perm = mapToBean(map,Permission.class);
        //1.通过传递的id查询权限
        Permission permission = permissionDao.findById(perm.getId()).get();
        permission.setName(perm.getName());
        permission.setDescription(perm.getDescription());
        permission.setEnVisible(perm.getEnVisible());
        permission.setCode(perm.getCode());

        //2.根据类型构造不同的资源
        int type = permission.getType();
        switch(type){
            case PermissionConstants.PY_MENU:
                PermissionMenu permissionMenu = mapToBean(map,PermissionMenu.class);
                permissionMenu.setId(perm.getId());
                permissionMenuDao.save(permissionMenu);
                break;
            case PermissionConstants.PY_POINT:
                PermissionPoint permissionPoint = mapToBean(map,PermissionPoint.class);
                permissionPoint.setId(perm.getId());
                permissionPointDao.save(permissionPoint);
                break;
            case PermissionConstants.PY_API:
                PermissionApi permissionApi = mapToBean(map,PermissionApi.class);
                permissionApi.setId(perm.getId());
                permissionApiDao.save(permissionApi);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);

        }

        permissionDao.save(permission);

    }


    public void deleteById(String id) throws CommonException {
        //1.查询权限
        Permission permission = permissionDao.findById(id).get();
        int type = permission.getType();

        Object object = null;
        if(type == PermissionConstants.PY_MENU){
           permissionMenuDao.deleteById(id);
        }else if(type == PermissionConstants.PY_API){
            permissionApiDao.deleteById(id);
        }else if(type == PermissionConstants.PY_POINT){
            permissionPointDao.deleteById(id);
        }else{
            throw new CommonException(ResultCode.FAIL);
        }
        permissionDao.deleteById(id);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public Map<String, Object> findById(String id) throws CommonException {
        //1.查询权限
        Permission permission = permissionDao.findById(id).get();
        int type = permission.getType();

        Object object = null;
        if(type == PermissionConstants.PY_MENU){
            object = permissionMenuDao.findById(id);
        }else if(type == PermissionConstants.PY_API){
            object = permissionApiDao.findById(id);
        }else if(type == PermissionConstants.PY_POINT){
            object = permissionMenuDao.findById(id);
        }else{
            throw new CommonException(ResultCode.FAIL);
        }

        Map<String, Object> map = BeanMapUtils.beanToMap(object);
        map.put("name",permission.getName());
        map.put("type",permission.getType());
        map.put("code",permission.getCode());
        map.put("description",permission.getDescription());
        map.put("pid",permission.getPid());
        map.put("enVisible",permission.getEnVisible());
        map.put("id",permission.getId());

        return map;
    }


    /**
     * 查询权限列表
     *  type : 0 菜单+按钮 1.菜单 2按钮 3 api接口
     *  enVisible: 0查询搜友saas平台的最高权限， 1：查询企业的权限
     */
    public List<Permission> findAll(Map<String,Object> map){
        //1.需要查询条件
        Specification<Permission> spec = new Specification<Permission>() {
            /**
             * 动态拼接条件
             * @param root
             * @param criteriaQuery
             * @param criteriaBuilder
             * @return
             */
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                //根据请求的父id是否为空构造查询条件
                if(!StringUtils.isEmpty(map.get("pid"))){
                    list.add(criteriaBuilder.equal(root.get("pid").as(String.class),(String)map.get("pid")));
                }

                //根据请求的enVisible是否为空构造查询条件
                if(!StringUtils.isEmpty(map.get("enVisible"))){
                    list.add(criteriaBuilder.equal(root.get("enVisible").as(String.class),(String)map.get("enVisible")));
                }

                if(!StringUtils.isEmpty(map.get("type"))){
                    String type= (String)map.get("type");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("type"));
                    if("0".equals(type)){
                        in.value(1).value(2);
                    }else{
                        in.value(Integer.parseInt(type));
                    }
                }

                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };


        return permissionDao.findAll(spec);
    }
}
