package com.ntu.system.controller;

import com.ntu.commom.entity.Result;
import com.ntu.commom.entity.ResultCode;
import com.ntu.commom.exception.CommonException;
import com.ntu.domain.system.Permission;
import com.ntu.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/sys")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;


    /**
     * 保存
     */
    @PostMapping("/permission")
    public Result save(@RequestBody Map<String,Object> map) throws Exception {
        permissionService.add(map);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 查询列表
     */
    @GetMapping(value="/permission")
    public Result getAll(@RequestParam Map<String, Object> map){
        List<Permission> permissions = permissionService.findAll(map);
        return new Result(ResultCode.SUCCESS,permissions);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/permission/{id}")
    public Result findById(@PathVariable("id")String id) throws CommonException {
        Map<String, Object> map = permissionService.findById(id);
        return new Result(ResultCode.SUCCESS,map);
    }

    /**
     * 修改
     */
    @PutMapping("/permission/{id}")
    public Result update(@PathVariable("id")String id, @RequestBody Map<String, Object> map) throws Exception {
        map.put("id",id);
        permissionService.update(map);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据ID删除
     */
    @DeleteMapping("/permission/{id}")
    public Result deleteById(@PathVariable("id")String id) throws CommonException {
        permissionService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }
}
