package com.thoroldvix.pricepal.shared;

import static com.thoroldvix.pricepal.shared.ValidationUtils.isNumber;

public final class ServerSearchCriteriaBuilder {

    private ServerSearchCriteriaBuilder() {
    }

    public static SearchCriteria getJoinCriteria(String serverIdentifier, String tableName) {
       boolean isNumber = isNumber(serverIdentifier);
       return SearchCriteria.builder()
               .column(isNumber ? "id" : "uniqueName")
               .operation(SearchCriteria.Operation.EQUALS)
               .value(serverIdentifier)
               .joinTable(tableName)
               .build();
    }

}
