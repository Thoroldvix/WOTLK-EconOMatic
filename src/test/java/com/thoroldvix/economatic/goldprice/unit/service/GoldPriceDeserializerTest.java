package com.thoroldvix.economatic.goldprice.unit.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.error.GoldPriceParsingException;
import com.thoroldvix.economatic.goldprice.service.GoldPriceDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
class GoldPriceDeserializerTest {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String JSON = """
            {
            "code": 2000,
            "messages": [],
            "payload": {
            "results": [
            {
            "converted_unit_price": 0.000624,
            "display_currency": "USD",
            "title": "Giantstalker [EU] - Horde"
            },
            {
            "converted_unit_price": 0.000745,
            "display_currency": "USD",
            "title": "Whitemane [US] - Alliance"
            }]
            }}""";

    private final GoldPriceDeserializer deserializer = new GoldPriceDeserializer();

    private static Stream<Arguments> provideJsonForTesting() {
        return Stream.of(
                Arguments.of("""
                        {
                        "payloa": {
                        }}""", "payload"),
                Arguments.of("""
                        {
                        "payload": {
                        "result": []
                        }}""", "results"),
                Arguments.of("""
                        {
                        "payload": {
                        "results": [
                        {
                        "converted_unit_price": 0.000624,
                        "display_currency": "USD",
                        "tite": "Giantstalker [EU] - Horde"
                        }]
                        }}""", "title"),
                Arguments.of("""
                        {
                        "payload": {
                        "results": [
                        {
                        "converted_unit_pric": 0.000624,
                        "display_currency": "USD",
                        "title": "Giantstalker [EU] - Horde"
                        }]
                        }}""", "converted_unit_price")
        );
    }

    @Test
    void deserialize_returnsListOfGoldPriceResponse() throws IOException {
        JsonParser jp = MAPPER.getFactory().createParser(JSON);
        DeserializationContext ctxt = MAPPER.getDeserializationContext();
        GoldPriceResponse goldPriceResponse1 = GoldPriceResponse.builder()
                .price(BigDecimal.valueOf(0.000624))
                .server("giantstalker-horde")
                .build();
        GoldPriceResponse goldPriceResponse2 = GoldPriceResponse.builder()
                .price(BigDecimal.valueOf(0.000745))
                .server("whitemane-alliance")
                .build();
        List<GoldPriceResponse> actual = deserializer.deserialize(jp, ctxt);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(List.of(goldPriceResponse1, goldPriceResponse2));
    }

    @Test
    void deserialize_throwsNullPointerException_whenJsonParserNull() {
        JsonParser jp = null;
        DeserializationContext ctxt = MAPPER.getDeserializationContext();
        assertThatThrownBy(() -> deserializer.deserialize(jp, ctxt))
                .isInstanceOf(NullPointerException.class);

    }

    @Test
    void deserialize_throwsNullPointerException_whenDeserializationContextNull() throws IOException {
        JsonParser jp = MAPPER.getFactory().createParser(JSON);
        DeserializationContext ctxt = null;
        assertThatThrownBy(() -> deserializer.deserialize(jp, ctxt))
                .isInstanceOf(NullPointerException.class);

    }

    @ParameterizedTest
    @MethodSource("provideJsonForTesting")
    void testDeserializeThrowsGoldPriceParsingExceptionWhenNodeNotValid(String json, String expectedMessage) throws IOException {
        JsonParser jp = MAPPER.getFactory().createParser(json);
        DeserializationContext ctxt = MAPPER.getDeserializationContext();
        assertThatThrownBy(() -> deserializer.deserialize(jp, ctxt))
                .isInstanceOf(GoldPriceParsingException.class)
                .hasMessage("Received JSON doesn't contain expected node: " + expectedMessage);
    }
}