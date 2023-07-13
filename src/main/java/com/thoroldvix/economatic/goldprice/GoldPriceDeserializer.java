package com.thoroldvix.economatic.goldprice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;


class GoldPriceDeserializer extends StdDeserializer<List<GoldPriceResponse>> {

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

    private static JsonNode getJsonNodeOrThrow(JsonNode rootNode, String nodeName) {
        return Optional.ofNullable(rootNode.get(nodeName))
                .orElseThrow(() -> new GoldPriceParsingException("Received JSON doesn't contain expected node: " + nodeName));
    }

    @Override
    public List<GoldPriceResponse> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        requireNonNull(jp, "Json parser cannot be null");
        requireNonNull(ctxt, "Deserialization context cannot be null");

        JsonNode rootNode = jp.getCodec().readTree(jp);
        return processRootNode(rootNode);
    }

    private List<GoldPriceResponse> processRootNode(JsonNode rootNode) {
        JsonNode payload = getJsonNodeOrThrow(rootNode, ROOT_NODE_PAYLOAD);
        JsonNode resultsNode = getJsonNodeOrThrow(payload, RESULTS_ARRAY);

        return resultsNode.isArray() ? StreamSupport.stream(resultsNode.spliterator(), false)
                .map(this::buildServerPrice)
                .toList() : new ArrayList<>();
    }

    private GoldPriceResponse buildServerPrice(JsonNode resultNode) {
        String serverName = getJsonNodeOrThrow(resultNode, NODE_TITLE).asText();
        BigDecimal price = getJsonNodeOrThrow(resultNode, NODE_PRICE).decimalValue();

        String formattedServerName = formatServerName(serverName);
        return GoldPriceResponse.builder()
                .server(formattedServerName)
                .price(price)
                .build();
    }

    private String formatServerName(String serverName) {
        Matcher serverNameMatcher = SERVER_NAME_PATTERN.matcher(serverName.replace("'", ""));

        return Optional.of(serverNameMatcher)
                .filter(Matcher::find)
                .map(this::buildFormattedServerName)
                .orElse(serverName);
    }

    private String buildFormattedServerName(Matcher serverNameMatcher) {
        String server = serverNameMatcher.group(1).toLowerCase().replaceAll(SERVER_NAME_REPLACE_REGEX, SERVER_NAME_FACTION_SEPARATOR);
        String faction = serverNameMatcher.group(2).toLowerCase();

        return server + SERVER_NAME_FACTION_SEPARATOR + faction;
    }
}