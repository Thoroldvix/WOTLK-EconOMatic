package com.thoroldvix.pricepal.shared;

import static com.thoroldvix.pricepal.shared.ValidationUtils.isNumber;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validatePositiveInt;

public final class ServerSearchCriteriaBuilder {

    private ServerSearchCriteriaBuilder() {
    }

    public static SearchCriteria getJoinCriteria(String serverIdentifier) {
        try {
            int serverId = Integer.parseInt(serverIdentifier);
            validatePositiveInt(serverId, "Server id must be positive");
            return getJoinCriteriaForServerId(serverId);
        } catch (NumberFormatException e){
            return getJoinCriteriaForServerUniqueName(serverIdentifier);
        }
    }

    private static SearchCriteria getJoinCriteriaForServerId(int serverId) {
       return SearchCriteria.builder()
                .column("id")
                .joinTable("server")
                .value(String.valueOf(serverId))
                .operation(SearchCriteria.Operation.EQUALS)
                .build();
    }

    private static SearchCriteria getJoinCriteriaForServerUniqueName(String uniqueName) {
        return SearchCriteria.builder()
                .column("uniqueName")
                .joinTable("server")
                .value(uniqueName)
                .operation(SearchCriteria.Operation.EQUALS)
                .build();

    }
}
