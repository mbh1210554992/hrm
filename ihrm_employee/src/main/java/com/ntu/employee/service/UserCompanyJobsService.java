package com.ntu.employee.service;

import com.ntu.domain.employee.UserCompanyJobs;
import com.ntu.employee.dao.UserCompanyJobsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IDEA
 * Author:xzengsf
 * Date:2018/10/19 9:52
 * Description:
 */
@Service
public class UserCompanyJobsService {
    @Autowired
    private UserCompanyJobsDao userCompanyJobsDao;

    public void save(UserCompanyJobs jobsInfo) {
        userCompanyJobsDao.save(jobsInfo);
    }

    public UserCompanyJobs findById(String userId) {
        return userCompanyJobsDao.findByUserId(userId);
    }
}
