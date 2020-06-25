package com.ntu.company.controller;

import com.ntu.commom.entity.Result;
import com.ntu.commom.entity.ResultCode;
import com.ntu.company.service.CompanyService;
import com.ntu.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @PostMapping(value = "")
    public Result addCompany(@RequestBody Company company){
        companyService.add(company);
        return new Result(ResultCode.SUCCESS);
    }

    @GetMapping(value = "/{id}")
    public Result getCompany(@PathVariable("id") String id){
        Result result = new Result(ResultCode.SUCCESS);
        result.setData(companyService.findById(id));
        return result;
    }

    @GetMapping(value="")
    public Result getAllCompany(){
        Result result = new Result(ResultCode.SUCCESS);
        result.setData(companyService.findAll());
        return result;
    }

    @PutMapping(value = "/{id}")
    public Result updateCompany(@PathVariable("id")String id, @RequestBody Company company){
        company.setId(id);
        companyService.update(company);
        return new Result(ResultCode.SUCCESS);
    }

    @DeleteMapping(value = "/{id}")
    public Result deleteCompany(@PathVariable("id") String id){
        companyService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }
}
