package com.ntu.system.controller;

import com.ntu.commom.controller.BaseController;
import com.ntu.commom.entity.PageResult;
import com.ntu.commom.entity.Result;
import com.ntu.commom.entity.ResultCode;
import com.ntu.domain.system.Role;
import com.ntu.domain.system.response.RoleResult;
import com.ntu.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/sys")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;

    /**
     * 新增角色
     * @param role
     * @return
     */
    @PostMapping("/role")
    public Result save(@RequestBody Role role){
        role.setCompanyId(this.companyId);
        roleService.save(role);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 更新角色
     */
    @PutMapping("/role/{id}")
    public Result update(@PathVariable("id") String id,@RequestBody Role role){
        role.setId(id);
        roleService.update(role);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/role/{id}")
    public Result deleteById(@PathVariable("id") String id){
        roleService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }

    @GetMapping("/role/{id}")
    public Result findById(@PathVariable("id") String id){
        //添加角色的权限列表，用于展示当前的权限
        Role role = roleService.findById(id);
        RoleResult roleResult = new RoleResult(role);
        return new Result(ResultCode.SUCCESS,roleResult);
    }

    /**
     * 分页查询角色
     */
    @GetMapping("/role")
    public Result findByPage(@RequestParam("page")int page,@RequestParam("pagesize")int size){
        Page<Role> roles = roleService.findByPage(this.companyId,page,size);
        PageResult<Role> pr = new PageResult<>(roles.getTotalElements(),roles.getContent());
        return new Result(ResultCode.SUCCESS,pr);
    }

    /**
     * 分配权限
     * @param
     * @return
     */
    @PutMapping("/role/assignPrem")
    public Result save(@RequestBody Map<String, Object> map){
        //1.获取被分配的用户id
        String roleId = (String)map.get("id");
        //2.获取权限的id列表
        List<String> permIds = (List<String>)map.get("permIds");
        //3.调用service完成角色分配
        roleService.assignPerms(roleId, permIds);
        return new Result(ResultCode.SUCCESS);
    }

    @GetMapping("role/list")
    public Result finadAll(){
        String companyId = this.companyId;
        List<Role> roles = roleService.findAll(companyId);
        return new Result(ResultCode.SUCCESS, roles);
    }


}
