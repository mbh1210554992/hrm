package com.ntu.commom.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected String companyId;
    protected String companyName;
    protected String userId;


    @ModelAttribute
    public void setResAndReq(HttpServletRequest request,HttpServletResponse response){
        this.request = request;
        this.response = response;

        //获取session中的安全数据
        //Subject subject = SecurityUtils.getSubject();
        //subject获取所有的数据集合
       // PrincipalCollection principals = subject.getPrincipals();
        /*if(principals != null && !principals.isEmpty()){
            //获取安全数据
            ProfileResult result = (ProfileResult)principals.getPrimaryPrincipal();
            this.companyId = result.getCompanyId();
            this.companyName = result.getCompany();
            this.userId = result.getUserId();
        }*/
        this.companyId = "1";
        this.companyName = "test";
        this.userId = "";

    }


}
