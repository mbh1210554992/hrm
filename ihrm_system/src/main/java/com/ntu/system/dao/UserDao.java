package com.ntu.system.dao;

import com.ntu.domain.system.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserDao extends JpaRepository<User,String>, JpaSpecificationExecutor<User> {
    User findByMobile(String mobil);
}
