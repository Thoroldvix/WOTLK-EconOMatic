package com.thoroldvix.economatic.error;

public enum ErrorMessages {
    SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY("Server identifier cannot be null or empty"),
    REGION_NAME_CANNOT_BE_NULL_OR_EMPTY("Region name cannot be null or empty"),
    FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY("Faction name cannot be null or empty"),
    PAGEABLE_CANNOT_BE_NULL("Pageable cannot be null"),
    TIME_RANGE_CANNOT_BE_NULL("Time range cannot be null"),
    SEARCH_REQUEST_CANNOT_BE_NULL("Search request cannot be null"),
    SEARCH_CRITERIA_CANNOT_BE_NULL_OR_EMPTY("Search criteria cannot be null or empty"),
    ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY("Item identifier cannot be null or empty"),
    NO_STATISTICS_FOUND("No statistics found");

    public final String message;

    ErrorMessages(String message) {
        this.message = message;
    }
}
