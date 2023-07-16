package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

class EqualsPredicateFactory implements PredicateFactory {

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        if (columnType.isEnum()) {
            int searchValue = getValueForEnumType(columnType, value);
            return cb.equal(columnPath, searchValue);
        }
        return cb.equal(columnPath, value);
    }

    private static <E extends Enum<E>> int getValueForEnumType(Class<?> enumClass, String value) {
        @SuppressWarnings("unchecked")
        Class<E> castedEnumClass = (Class<E>) enumClass;
        E enumValue = Enum.valueOf(castedEnumClass, value.toUpperCase());
        return enumValue.ordinal();
    }
}