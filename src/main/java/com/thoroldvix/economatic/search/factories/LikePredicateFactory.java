package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

class LikePredicateFactory implements PredicateFactory {
    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        return cb.like(columnPath.as(String.class), "%" + value + "%");
    }
}
