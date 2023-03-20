package com.example.g2gcalculator.service;

import com.example.g2gcalculator.model.Price;
import org.springframework.data.jpa.domain.Specification;

public class PriceSpecification {
    public static Specification<Price> matchId(Integer realmId){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("realm"), realmId);
    }

}