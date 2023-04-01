package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.item.ItemPriceCalculatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ItemPriceCalculatorImplTest {

    private final ItemPriceCalculatorImpl itemPriceCalculatorImpl = new ItemPriceCalculatorImpl();


    @Test
    void calculatePrice_whenValidPriceResponse_returnsCorrectItemPrice() {
        PriceResponse priceResponse = PriceResponse.builder()
                .currency("USD")
                .value(BigDecimal.valueOf(1000))
                .build();
        long targetPrice = 10000;
        int amount = 1;
        BigDecimal expectedValue = BigDecimal.valueOf(1000).setScale(6, RoundingMode.CEILING);
        BigDecimal actualValue = itemPriceCalculatorImpl.calculatePrice(targetPrice, priceResponse, amount);

        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void calculatePrice_whenAmountLessThanOne_throwsIllegalArgumentException() {
        PriceResponse priceResponse = PriceResponse.builder()
                .currency("USD")
                .value(BigDecimal.valueOf(1000))
                .build();
        long targetPrice = 10000;
        int amount = -1;

        assertThatThrownBy(() -> itemPriceCalculatorImpl.calculatePrice(targetPrice, priceResponse, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }
     @Test
    void calculatePrice_whenPriceResponseContainsInvalidValue_throwsIllegalArgumentException() {
        PriceResponse priceResponse = PriceResponse.builder()
                .currency("USD")
                .value(BigDecimal.valueOf(-1))
                .build();
        long targetPrice = 10000;
        int amount = 1;

        assertThatThrownBy(() -> itemPriceCalculatorImpl.calculatePrice(targetPrice, priceResponse, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void calculatePrice_whenTargetPriceIsInvalid_throwsIllegalArgumentException() {
        PriceResponse priceResponse = PriceResponse.builder()
                .currency("USD")
                .value(BigDecimal.valueOf(1000))
                .build();
        long targetPrice = -1;
        int amount = 1;

        assertThatThrownBy(() -> itemPriceCalculatorImpl.calculatePrice(targetPrice, priceResponse, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

}