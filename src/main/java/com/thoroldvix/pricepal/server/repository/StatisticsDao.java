package com.thoroldvix.pricepal.server.repository;

import com.thoroldvix.pricepal.server.entity.Population;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class StatisticsDao<T> {
    @PersistenceContext
    private final EntityManager entityManager;


    public Map<String, Object> getStatsForSpec(Specification<T> spec,
                                               Class<T> entityClass) {
        Objects.requireNonNull(spec, "Specification cannot be null");
        Objects.requireNonNull(entityClass, "Entity class cannot be null");
        if (Population.class.equals(entityClass)) {
            return getStatisticsForEntity((Specification<Population>) spec, Population.class, "population", Integer.class);
        } else if (ServerPrice.class.equals(entityClass)) {
            return getStatisticsForEntity((Specification<ServerPrice>) spec, ServerPrice.class, "price", BigDecimal.class);
        }
        throw new IllegalArgumentException("Unsupported entity class");
    }

    private <E, N extends Number> Map<String, Object> getStatisticsForEntity(
            Specification<E> spec,
            Class<E> entityClass,
            String attribute,
            Class<N> numberClass) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = getMainQuery(spec, entityClass, attribute, numberClass, cb);

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        Tuple result = typedQuery.getSingleResult();
        Double average = result.get("average", Double.class);

        long count = result.get("count", Long.class);

        E max = getMax(spec, entityClass, attribute, cb);
        E min = getMin(spec, entityClass, attribute,  cb);

        return createStatistics(average, min, max, count);
    }

    private <E> E getById(Class<E> entityClass, CriteriaBuilder cb, Number id) {
        CriteriaQuery<E> minQuery = cb.createQuery(entityClass);
        Root<E> minRoot = minQuery.from(entityClass);
        minQuery.select(minRoot).where(cb.equal(minRoot.get("id"), id));
        return entityManager.createQuery(minQuery).getSingleResult();
    }

    private <E, N extends Number> Number getIdByOrder(Specification<E> spec,
                                                      Class<E> entityClass,
                                                      String attribute,
                                                      CriteriaBuilder cb,
                                                      boolean isAscending) {
        CriteriaQuery<Number> query = cb.createQuery(Number.class);
        Root<E> root = query.from(entityClass);
        query.select(root.get("id"));

        Predicate specPredicate = spec.toPredicate(root, query, cb);
        query.where(specPredicate);

        Path<N> attributePath = root.get(attribute);
        Order attributeOrder = isAscending ? cb.asc(attributePath) : cb.desc(attributePath);
        Order updatedAtOrder = cb.desc(root.get("updatedAt"));
        List<Order> orderList = List.of(attributeOrder, updatedAtOrder);
        query.orderBy(orderList);


        TypedQuery<Number> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);

        return typedQuery.getSingleResult();
    }

    private <E> E getMax(Specification<E> spec,
                         Class<E> entityClass,
                         String attribute,
                         CriteriaBuilder cb) {
        Number maxId = getIdByOrder(spec, entityClass, attribute,  cb, false);
        return getById(entityClass, cb, maxId);
    }

    private <E> E getMin(Specification<E> spec,
                         Class<E> entityClass,
                         String attribute,
                         CriteriaBuilder cb) {
        Number minId = getIdByOrder(spec, entityClass, attribute,  cb, true);
        return getById(entityClass, cb, minId);
    }

    private <E, N extends Number> CriteriaQuery<Tuple> getMainQuery(Specification<E> spec,
                                                                    Class<E> entityClass,
                                                                    String attribute,
                                                                    Class<N> numberClass,
                                                                    CriteriaBuilder cb) {

        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<E> root = query.from(entityClass);
        Expression<N> attributePath = root.get(attribute);
        Predicate filteringPredicate = cb.and(spec.toPredicate(root, query, cb));

        query.multiselect(
                cb.avg(attributePath).alias("average"),
                cb.count(root).alias("count")
        ).where(filteringPredicate);

        return query;
    }

    private <E, N extends Number> Map<String, Object> createStatistics(N average,
                                                                       E min,
                                                                       E max,
                                                                       long count) {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("avg", average);
        statistics.put("min", min);
        statistics.put("max", max);
        statistics.put("count", count);
        return statistics;
    }
}
