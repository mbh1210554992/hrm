package com.ntu.company.dao;

import com.ntu.domain.company.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentDao extends JpaRepository<Department, String>, JpaSpecificationExecutor<Department> {
    Department findByCodeAndCompanyId(String code, String companyId);
}
