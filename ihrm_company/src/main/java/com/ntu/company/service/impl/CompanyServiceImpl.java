package com.ntu.company.service.impl;

import com.ntu.commom.utils.IdWorker;
import com.ntu.company.dao.CompanyDao;
import com.ntu.company.service.CompanyService;
import com.ntu.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    CompanyDao companyDao;
    @Autowired
    IdWorker idWorker;

    /**
     * 保存企业
     */
    public void add(Company company){
        //基本属性设置
        String id = idWorker.nextId()+"";
        company.setId(id);
        company.setState(1);
        companyDao.save(company);
    }

    //更新企业
    @Override
    public void update(Company company) {
        Company temp = companyDao.findById(company.getId()).get();
        temp.setName(company.getName());
        companyDao.save(temp);
    }

    @Override
    public void deleteById(String id) {
        companyDao.deleteById(id);
    }

    @Override
    public Company findById(String id) {
        return companyDao.findById(id).get();
    }

    @Override
    public List<Company> findAll() {
        return companyDao.findAll();
    }

}
