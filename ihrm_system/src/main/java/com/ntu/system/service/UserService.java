package com.ntu.system.service;

import com.ntu.commom.entity.ResultCode;
import com.ntu.commom.exception.CommonException;
import com.ntu.commom.utils.IdWorker;
import com.ntu.domain.company.Department;
import com.ntu.domain.system.Role;
import com.ntu.domain.system.User;
import com.ntu.system.client.DepartmentFeignClient;
import com.ntu.system.dao.RoleDao;
import com.ntu.system.dao.UserDao;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    IdWorker idWorker;
    @Autowired
    RoleDao roleDao;
    @Autowired
    DepartmentFeignClient departmentFeignClient;

    /**
     * 批量用户保存
     */
    @Transactional
    public void saveAll(List<User> list,String companyId,String companyName){
        for(User user : list){
            //默认密码
            String password = new Md5Hash("123456",user.getMobile(),3).toString();
            user.setPassword(password);
            user.setId(idWorker.nextId()+"");
            user.setCompanyName(companyName);
            user.setCompanyId(companyId);
            user.setInServiceStatus(1);
            user.setEnableState(1);
            user.setLevel("user");
            user.setCreateTime(new Date());

            //填充部门的属性(需要去企业微服务中查询)
            Department department = departmentFeignClient.findByCode(user.getDepartmentId(),companyId);
            if(department != null){
                user.setDepartmentName(department.getName());
                user.setDepartmentId(department.getId());
            }
            userDao.save(user);
        }
    }
    /**
     * 保存用户
     */
    public void add(User user){
        //基本属性设置
        String id = idWorker.nextId()+"";
        String password = new Md5Hash("123456",user.getMobile(),3).toString();
        //能够新加的用户都是user角色的
        user.setLevel("user");
        user.setPassword(password);//初始密码
        user.setEnableState(1); //启用
        user.setId(id);
        userDao.save(user);
    }

    //更新用户

    public void update(User user) {
        User temp = userDao.findById(user.getId()).get();
        temp.setUsername(user.getUsername());
        temp.setPassword(user.getPassword());
        temp.setDepartmentId(user.getDepartmentId());
        temp.setDepartmentName(user.getDepartmentName());
        userDao.save(temp);
    }


    public void deleteById(String id) {
        userDao.deleteById(id);
    }


    public User findById(String id) {
        return userDao.findById(id).get();
    }


    /**
     * 查询用户列表
     *      参数：map集合的形式
     *          hasDept
     *          departmentId
     *          companyId
     */
    public Page findAll(Map<String,Object> map, int page, int size){
        //1.需要查询条件
        Specification<User> spec = new Specification<User>() {
            /**
             * 动态拼接条件
             * @param root
             * @param criteriaQuery
             * @param criteriaBuilder
             * @return
             */
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                //根据请求的companyId是否为空构造查询条件
                if(!StringUtils.isEmpty(map.get("companyId"))){
                    list.add(criteriaBuilder.equal(root.get("companyId").as(String.class),(String)map.get("companyId")));
                }

                //根据请求的departmentId是否为空构造查询条件
                if(!StringUtils.isEmpty(map.get("departmentId"))){
                    list.add(criteriaBuilder.equal(root.get("departmentId").as(String.class),(String)map.get("departmentId")));
                }

                if(!StringUtils.isEmpty(map.get("hasDept"))){
                    //根据请求的hasDept,是否分配部门 ， 0未分配(departmentId == null)  1已分配
                    if("0".equals((String)map.get("hasDept"))){
                        list.add(criteriaBuilder.isNull(root.get("departmentId")));
                    }else{
                        list.add(criteriaBuilder.isNotEmpty(root.get("departmentId")));
                    }
                }

                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };

        //2.分页
        Page<User> pageUser = userDao.findAll(spec, PageRequest.of(page - 1, size));
        return pageUser;
    }

    public void assignRoles(String userId, List<String> roleIds){
        //根据id查询用户
        User user = userDao.findById(userId).get();
            //2.设置用户的角色集合
            Set<Role> roles = new HashSet<>();
            for(String roleId : roleIds){
                Role role = roleDao.findById(roleId).get();
                roles.add(role);
            }
            //设置用户和角色的几集合关系
            user.setRoles(roles);

        //3.更新用户
        userDao.save(user);

    }

    /**
     * 通过mobile查询用户
     */
    public User findByMobile(String mobile){
        User user = userDao.findByMobile(mobile);
        return user;
    }

    public  String uploadImage(String userId, MultipartFile file) throws CommonException, IOException {
        User user = userDao.findById(userId).get();
        if(user == null){
            throw new CommonException(ResultCode.FAIL);
        }
        String data = Base64.encode(file.getBytes());
        String imgUrl = new String("data:image/png;base64,"+data);
        user.setStaffPhoto(imgUrl);
        userDao.save(user);
        return imgUrl;
    }

}
