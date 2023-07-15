package com.thoroldvix.economatic.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void notEmpty_collection() {
        List<String> nonEmptyList = Arrays.asList("a", "b", "c");
        assertThatCode(() -> ValidationUtils.notEmpty(nonEmptyList, () -> new IllegalArgumentException("Collection should not be empty")))
                .doesNotThrowAnyException();

        List<String> emptyList = new ArrayList<>();
        assertThatThrownBy(() -> ValidationUtils.notEmpty(emptyList, () -> new IllegalArgumentException("Collection should not be empty")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void notEmpty_array() {
        String[] nonEmptyArray = new String[] {"a", "b", "c"};
        assertThatCode(() -> ValidationUtils.notEmpty(nonEmptyArray, () -> new IllegalArgumentException("Array should not be empty")))
                .doesNotThrowAnyException();

        String[] emptyArray = new String[] {};
        assertThatThrownBy(() -> ValidationUtils.notEmpty(emptyArray, () -> new IllegalArgumentException("Array should not be empty")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isNonEmptyString() {
        assertThat(ValidationUtils.isNonEmptyString("Hello")).isTrue();
        assertThat(ValidationUtils.isNonEmptyString("")).isFalse();
    }

    @Test
    void notEmpty_string() {
        String nonEmptyString = "Hello";
        assertThatCode(() -> ValidationUtils.notEmpty(nonEmptyString, "String should not be empty"))
                .doesNotThrowAnyException();

        String emptyString = "";
        assertThatThrownBy(() -> ValidationUtils.notEmpty(emptyString, "String should not be empty"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void notLessThan() {
        int minValue = 5;
        assertThatCode(() -> ValidationUtils.notLessThan(5, minValue, "Value should not be less than " + minValue))
                .doesNotThrowAnyException();

        int lesserValue = 4;
        assertThatThrownBy(() -> ValidationUtils.notLessThan(lesserValue, minValue, "Value should not be less than " + minValue))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isCollectionEmpty() {
        List<String> nonEmptyList = Arrays.asList("a", "b", "c");
        assertThat(ValidationUtils.isCollectionEmpty(nonEmptyList)).isFalse();

        List<String> emptyList = new ArrayList<>();
        assertThat(ValidationUtils.isCollectionEmpty(emptyList)).isTrue();
    }

    @Test
    void checkNullAndGet() {
        String value = "Hello";
        assertThat(ValidationUtils.checkNullAndGet(() -> value)).isEqualTo(value);


        Object result = ValidationUtils.checkNullAndGet(() -> null);
        assertThat(result).isNull();
    }
}