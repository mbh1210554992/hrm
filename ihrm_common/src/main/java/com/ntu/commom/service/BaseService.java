package com.ntu.commom.service;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class BaseService<T> {
    protected Specification<T> getSpec(String companyId){
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //根据企业id查询
                return criteriaBuilder.equal(root.get("companyId").as(String.class),companyId);
            }
        };
        return spec;
    }
}
