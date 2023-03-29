package com.thoroldvix.g2gcalculator.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AhIDValidator implements ConstraintValidator<ValidAhID, Integer> {
    private static final int MAX_AH_ID = 476;
    private static final int MIN_AH_ID = 279;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value >= MIN_AH_ID && value <= MAX_AH_ID;
    }
}