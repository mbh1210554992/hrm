package com.ntu.company.service;

import com.ntu.domain.company.Company;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompanyService {
    void add(Company company);
    void update(Company company);
    void deleteById(String id);
    Company findById(String id);
    List<Company> findAll();
}
