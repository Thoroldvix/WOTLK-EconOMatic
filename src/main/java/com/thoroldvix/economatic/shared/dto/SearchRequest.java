package com.thoroldvix.economatic.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Arrays;

@Builder
@Schema(description = "Request body for filtering")
public record SearchRequest(
        @Schema(description = "Search criteria list for searching")
        @NotEmpty(message = "Search criteria cannot be null or empty")
        SearchCriteria[] searchCriteria,

        @Schema(description = "Global operator for combining search criteria", allowableValues = {"AND", "OR", "NOT"})
        GlobalOperator globalOperator
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchRequest that)) return false;
        if (!Arrays.equals(searchCriteria, that.searchCriteria)) return false;
        return globalOperator == that.globalOperator;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(searchCriteria);
        result = 31 * result + (globalOperator != null ? globalOperator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SearchRequest{" +
               "searchCriteria=" + Arrays.toString(searchCriteria) +
               ", globalOperator=" + globalOperator +
               '}';
    }

    public enum GlobalOperator {
        AND, OR, NOT
    }
}
