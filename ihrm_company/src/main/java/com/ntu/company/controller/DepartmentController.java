package com.ntu.company.controller;

import com.ntu.commom.controller.BaseController;
import com.ntu.commom.entity.Result;
import com.ntu.commom.entity.ResultCode;
import com.ntu.company.service.CompanyService;
import com.ntu.company.service.DepartmentService;
import com.ntu.domain.company.Company;
import com.ntu.domain.company.Department;
import com.ntu.domain.company.response.DeptListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//解决跨域
@CrossOrigin
@RestController
@RequestMapping(value = "/company" )
public class DepartmentController extends BaseController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CompanyService companyService;

    /**
     * 添加部门
     * @param department
     * @return
     */
    @PostMapping(value = "/department")
    public Result save(@RequestBody Department department){
        //设置保存的企业id
        department.setCompanyId(this.companyId);
        departmentService.save(department);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 更新部门信息
     * @param id
     * @param department
     */
    @PutMapping(value = "/department/{id}")
    public Result update(@PathVariable("id") String id, @RequestBody Department department){
        //设置修改的部门id
        department.setId(id);
        departmentService.update(department);
        return new Result(ResultCode.SUCCESS);

    }

    /**
     * 查询部门列表
     *      *    需要提供企业的id
     * @return
     */
    @GetMapping("/department")
    public Result findAll(){
        List<Department> departments = departmentService.findAll(this.companyId);
        //构造返回结果
        Company company = companyService.findById(this.companyId);
        DeptListResult deptListResult = new DeptListResult(company,departments);
        return new Result(ResultCode.SUCCESS,deptListResult);
    }

    /**
     * 通过部门id查询
     * @return
     */
    @GetMapping("/department/{departmentId}")
    public Result findById(@PathVariable("departmentId")String id){
        Department department = departmentService.findById(id);
        return new Result(ResultCode.SUCCESS,department);
    }

    /**
     * 通过id删除部门
     * @param id
     * @return
     */
    @DeleteMapping("/department/{departmentId}")
    public Result deleteById(@PathVariable("departmentId") String id){
        departmentService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据部门code返回部门id
     * @param code
     * @param companyId
     * @return
     */
    @GetMapping("/department/search")
    public Department findByCode(@RequestParam(value = "code")String code, @RequestParam("companyId")String companyId){
        Department department = departmentService.findByCode(code,companyId);
        return department;
    }
}
