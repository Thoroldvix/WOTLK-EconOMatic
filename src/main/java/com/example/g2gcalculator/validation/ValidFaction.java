package com.example.g2gcalculator.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FactionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFaction {
    String message() default "{g2gcalc.constraints.faction.ValidFaction.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}