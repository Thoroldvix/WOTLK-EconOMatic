package com.thoroldvix.economatic.goldprice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Validated
public class GoldPriceDeserializer extends StdDeserializer<List<GoldPriceResponse>> {

    private static final String SERVER_NAME_REGEX = "^(\\w+(?:\\s\\w+)?)\\s.*-\\s(\\w+)$";


    public GoldPriceDeserializer() {
        this(null);
    }

    public GoldPriceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public List<GoldPriceResponse> deserialize(
            @NotNull(message = "Json parse cannot be null")
            JsonParser jp,
            @NotNull(message = "Deserialization context cannot be null")
            DeserializationContext ctxt) throws IOException {

        JsonNode rootNode = jp.getCodec().readTree(jp);
        return processRootNode(rootNode);
    }

    private List<GoldPriceResponse> processRootNode(JsonNode rootNode) {
        List<GoldPriceResponse> prices = new ArrayList<>();
        JsonNode resultsNode = rootNode.get("payload").get("results");
        if (resultsNode.isArray()) {
            resultsNode.forEach(resultNode -> {
                GoldPriceResponse serverPrice = buildServerPrice(resultNode);
                prices.add(serverPrice);
            });
        }
        return prices;
    }

    private GoldPriceResponse buildServerPrice(JsonNode resultNode) {
        String serverName = formatServerName(resultNode.get("title").asText());
        BigDecimal price = resultNode.get("converted_unit_price").decimalValue();

        return GoldPriceResponse.builder()
                .server(serverName)
                .price(price)
                .build();
    }

    private String formatServerName(String serverName) {
        Matcher serverNameMatcher = Pattern.compile(SERVER_NAME_REGEX).matcher(serverName.replace("'", ""));

        if (serverNameMatcher.find()) {
            return buildFormattedServerName(serverNameMatcher);
        }
        return serverName;
    }

    private String buildFormattedServerName(Matcher serverNameMatcher) {
        String server = serverNameMatcher.group(1).toLowerCase().replaceAll("\\s+", "-");
        String faction = serverNameMatcher.group(2).toLowerCase();
        return server + "-" + faction;
    }
}