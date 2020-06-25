package com.ntu.company.service.impl;

import com.ntu.commom.service.BaseService;
import com.ntu.commom.utils.IdWorker;
import com.ntu.company.dao.DepartmentDao;
import com.ntu.company.service.DepartmentService;
import com.ntu.domain.company.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl extends BaseService implements DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private IdWorker idWorker;

    @Override
    public void save(Department department) {
        String id = idWorker.nextId()+"";
        department.setId(id);
        departmentDao.save(department);
    }

    @Override
    public void update(Department department) {
        //1.根据id查询部门
        Department temp = departmentDao.findById(department.getId()).get();
        //2.设置更新后的部门属性
        temp.setCode(department.getCode());
        temp.setIntroduce(department.getIntroduce());
        temp.setName(department.getName());
        //3.更新部门
        departmentDao.save(temp);
    }

    @Override
    public Department findById(String id) {
        return departmentDao.findById(id).get();
    }

    @Override
    public void deleteById(String id) {
        departmentDao.deleteById(id);
    }

    @Override
    public List<Department> findAll(String companyId) {
        /**
         * 用户构造查询条件
         *      1.只查询companyId
         *      2.许多地方都用到companyId查询
         *      3.很多对象都具有companyId属性
         */
//        Specification<Department> spec = new Specification<Department>() {
//            /**
//             * 构造查询条件
//             * @param root   包含了所有的对象属性
//             * @param criteriaQuery   高级查询
//             * @param criteriaBuilder  构造查询条件
//             * @return
//             */
//            @Override
//            public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                //根据企业id查询
//                return criteriaBuilder.equal(root.get("companyId").as(String.class),companyId);
//
//            }
//        };
        return departmentDao.findAll(getSpec(companyId));
    }

    @Override
    public Department findByCode(String code, String companyId) {
        return departmentDao.findByCodeAndCompanyId(code,companyId);
    }
}
