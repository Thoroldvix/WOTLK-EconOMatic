package com.thoroldvix.economatic.population;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class PopulationDeserializer {
    private final ObjectMapper mapper;

    public PopulationDeserializer() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<TotalPopResponse> extractFromJson(String populationJson) {
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, TotalPopResponse.class);
        try {
            return mapper.readValue(populationJson, collectionType);
        } catch (JsonProcessingException e) {
            throw new PopulationParsingException("Error while parsing population json " + e.getMessage());
        }
    }
}