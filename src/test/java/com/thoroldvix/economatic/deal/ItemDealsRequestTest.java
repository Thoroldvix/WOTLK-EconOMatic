package com.thoroldvix.economatic.deal;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDealsRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void whenAllFieldsAreCorrect_thenZeroConstraintViolations() {
        ItemDealsRequest request = new ItemDealsRequest("server", 2, 2, 2);

        Set<ConstraintViolation<ItemDealsRequest>> constraintViolations = validator.validate(request);

        assertThat(constraintViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void whenServerIdentifierIsEmpty_thenOneConstraintViolation(String serverIdentifier) {
        ItemDealsRequest request = new ItemDealsRequest(serverIdentifier, 2, 2, 2);

        Set<ConstraintViolation<ItemDealsRequest>> constraintViolations = validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    void whenMinQuantityLessThanOne_thenOneConstraintViolationException() {
        ItemDealsRequest request = new ItemDealsRequest("server1", 0, 2, 2);

        Set<ConstraintViolation<ItemDealsRequest>> constraintViolations = validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    void whenMinQualityLessThanZero_thenOneConstraintViolationException() {
        ItemDealsRequest request = new ItemDealsRequest("server1", 1, -1, 2);

        Set<ConstraintViolation<ItemDealsRequest>> constraintViolations = validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    void whenLimitLessThanOne_thenOneConstraintViolationException() {
        ItemDealsRequest request = new ItemDealsRequest("server1", 1, 2, 0);

        Set<ConstraintViolation<ItemDealsRequest>> constraintViolations = validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
    }
}