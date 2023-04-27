package com.thoroldvix.g2gcalculator.common;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class StringFormatter {

   public static String formatString(String input, Function<String, String> wordFormatter) {
    return Arrays.stream(input.trim().split("\\s+"))
            .map(wordFormatter)
            .collect(Collectors.joining("-"))
            .toLowerCase();
}

}
