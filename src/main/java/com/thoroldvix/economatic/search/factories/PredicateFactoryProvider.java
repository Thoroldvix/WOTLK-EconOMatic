package com.thoroldvix.economatic.search.factories;

import com.thoroldvix.economatic.search.SearchCriteria;

import java.util.EnumMap;
import java.util.Map;

public final class PredicateFactoryProvider {
    private static final Map<SearchCriteria.Operation, PredicateFactory> FACTORIES_MAP = new EnumMap<>(SearchCriteria.Operation.class);

    static {
        FACTORIES_MAP.put(SearchCriteria.Operation.EQUALS, new EqualsPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.IN, new InPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.LIKE, new LikePredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.BETWEEN_NUMERIC, new BetweenNumericPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.LESS_THAN, new LessThanPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.GREATER_THAN, new GreaterThanPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.EQUALS_IGNORE_CASE, new EqualsIgnoreCasePredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.LESS_THAN_OR_EQUALS, new LessThanOrEqualsPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.GREATER_THAN_OR_EQUALS, new GreaterThanOrEqualsPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.AFTER, new AfterPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.BEFORE, new BeforePredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.AFTER_OR_EQUALS, new AfterOrEqualsPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.BEFORE_OR_EQUALS, new BeforeOrEqualsPredicateFactory());
        FACTORIES_MAP.put(SearchCriteria.Operation.BETWEEN_DATE_TIME, new BetweenDateTimePredicateFactory());
    }

    private PredicateFactoryProvider(){}

    public static PredicateFactory getPredicateFactory(SearchCriteria searchCriteria) {
        return FACTORIES_MAP.get(searchCriteria.operation());
    }
}
