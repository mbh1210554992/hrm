package com.ntu.system.dao;

import com.ntu.domain.system.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleDao extends JpaRepository<Role,String>, JpaSpecificationExecutor<Role> {
}
