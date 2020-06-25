package com.ntu.company.service;

import com.ntu.domain.company.Department;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService  {
    void save(Department department);
    void update(Department department);
    Department findById(String id);
    void deleteById(String id);
    List<Department> findAll(String companyId);
    Department findByCode(String Code,String companyId);
}
