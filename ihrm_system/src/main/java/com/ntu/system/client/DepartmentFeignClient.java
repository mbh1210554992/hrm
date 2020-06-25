package com.ntu.system.client;

import com.ntu.commom.entity.Result;
import com.ntu.domain.company.Department;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 生声明接口，通过fegin调用其它微服务
 */
@FeignClient(value = "ihrm-company") //制定需要调用的微服务的名称
public interface DepartmentFeignClient {
    /**
     * 调用企业微服务的接口
     */
    @GetMapping("/company/department/{departmentId}")
    public Result findById(@PathVariable("departmentId")String id);

    @GetMapping("/company/department/search")
    public Department findByCode(@RequestParam(value = "code")String code, @RequestParam("companyId")String companyId);
}
