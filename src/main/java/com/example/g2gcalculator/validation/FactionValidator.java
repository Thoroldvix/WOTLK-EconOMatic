package com.example.g2gcalculator.validation;

import com.example.g2gcalculator.model.Faction;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FactionValidator implements ConstraintValidator<ValidFaction, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
         for (Faction f : Faction.values()) {
            if (f.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}