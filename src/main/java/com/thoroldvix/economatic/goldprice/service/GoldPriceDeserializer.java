package com.thoroldvix.economatic.goldprice.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;


public class GoldPriceDeserializer extends StdDeserializer<List<GoldPriceResponse>> {

    private static final Pattern SERVER_NAME_PATTERN = Pattern.compile("^(\\w+(?:\\s\\w+)?)\\s.*-\\s(\\w+)$");
    private static final String SERVER_NAME_REPLACE_REGEX = "\\s+";
    private static final String SERVER_NAME_FACTION_SEPARATOR = "-";
    private static final String ROOT_NODE_PAYLOAD = "payload";
    private static final String RESULTS_ARRAY = "results";
    private static final String NODE_TITLE = "title";
    private static final String NODE_PRICE = "converted_unit_price";


    public GoldPriceDeserializer() {
        this(null);
    }

    public GoldPriceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public List<GoldPriceResponse> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        requireNonNull(jp, "Json parser cannot be null");
        requireNonNull(ctxt, "Deserialization context cannot be null");

        JsonNode rootNode = jp.getCodec().readTree(jp);
        return processRootNode(rootNode);
    }

    private List<GoldPriceResponse> processRootNode(JsonNode rootNode) {
        JsonNode resultsNode = rootNode.get(ROOT_NODE_PAYLOAD).get(RESULTS_ARRAY);

        return resultsNode.isArray() ? StreamSupport.stream(resultsNode.spliterator(), false)
                .map(this::buildServerPrice)
                .toList() : new ArrayList<>();
    }

    private GoldPriceResponse buildServerPrice(JsonNode resultNode) {
        String serverName = formatServerName(resultNode.get(NODE_TITLE).asText());
        BigDecimal price = resultNode.get(NODE_PRICE).decimalValue();

        return GoldPriceResponse.builder()
                .server(serverName)
                .price(price)
                .build();
    }

    private String formatServerName(String serverName) {
        Matcher serverNameMatcher = SERVER_NAME_PATTERN.matcher(serverName.replace("'", ""));

        if (serverNameMatcher.find()) {
            return buildFormattedServerName(serverNameMatcher);
        }

        return serverName;
    }

    private String buildFormattedServerName(Matcher serverNameMatcher) {
        String server = serverNameMatcher.group(1).toLowerCase().replaceAll(SERVER_NAME_REPLACE_REGEX, SERVER_NAME_FACTION_SEPARATOR);
        String faction = serverNameMatcher.group(2).toLowerCase();

        return server + SERVER_NAME_FACTION_SEPARATOR + faction;
    }
}