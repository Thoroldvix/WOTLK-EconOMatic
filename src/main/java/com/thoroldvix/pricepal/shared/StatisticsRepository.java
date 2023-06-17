package com.thoroldvix.pricepal.shared;

import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public interface StatisticsRepository<E> {

    Map<String, Object> getStats(Specification<E> spec,
                                 Class<E> entityClass);

}
