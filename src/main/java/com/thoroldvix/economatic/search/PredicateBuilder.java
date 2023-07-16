package com.thoroldvix.economatic.search;

import com.thoroldvix.economatic.search.factories.PredicateFactory;
import com.thoroldvix.economatic.search.factories.PredicateFactoryProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public final class PredicateBuilder {

    private PredicateBuilder() {
    }

    public static Predicate buildPredicate(CriteriaBuilder cb, SearchCriteria searchCriteria, Path<?> columnPath) {
        String value = searchCriteria.value();
        PredicateFactory factory = PredicateFactoryProvider.getPredicateFactory(searchCriteria);
        return factory.getPredicate(cb, columnPath, value);
    }
}
