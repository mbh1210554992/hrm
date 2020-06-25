package com.ntu.employee.service;

import com.ntu.domain.employee.UserCompanyPersonal;
import com.ntu.domain.employee.response.EmployeeReportResult;
import com.ntu.employee.dao.UserCompanyPersonalDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 */
@Service
public class UserCompanyPersonalService {
    @Autowired
    private UserCompanyPersonalDao userCompanyPersonalDao;

    public void save(UserCompanyPersonal personalInfo) {
        userCompanyPersonalDao.save(personalInfo);
    }

    public UserCompanyPersonal findById(String userId) {
        return userCompanyPersonalDao.findByUserId(userId);
    }

    public List<EmployeeReportResult> findByReport(String companyId,String month){
        return userCompanyPersonalDao.findByReport(companyId, month+"%");
    }
}
