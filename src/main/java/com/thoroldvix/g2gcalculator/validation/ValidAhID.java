package com.thoroldvix.g2gcalculator.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = AhIDValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAhID {
    String message() default "Invalid AH ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}