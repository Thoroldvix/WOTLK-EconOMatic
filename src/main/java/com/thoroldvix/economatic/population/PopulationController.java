package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.population.dto.PopulationResponse;
import com.thoroldvix.economatic.population.dto.PopulationPageResponse;
import com.thoroldvix.economatic.population.dto.PopulationListResponse;
import com.thoroldvix.economatic.population.dto.TotalPopResponse;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import com.thoroldvix.economatic.shared.dto.TimeRange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
@Tag(name = "Populations API", description = "API for retrieving server population")
@RequiredArgsConstructor
@RequestMapping("/wow-classic/api/v1/servers/populations")
public class PopulationController {

    private final PopulationService populationService;


    @Operation(summary = "Retrieve all populations",
            description = "Returns all populations within the specified time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No populations found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationPageResponse> getAll(@Parameter(description = "Time range in days to retrieve populations for",
    example = "7")
                                                           @RequestParam(defaultValue = "7") int timeRange,
                                                         @PageableDefault(size = 100, sort = "updatedAt", direction = Sort.Direction.DESC)
                                                           @ParameterObject Pageable pageable) {
        var allPopulations = populationService.getAll(new TimeRange(timeRange), pageable);
        return ResponseEntity.ok(allPopulations);
    }

    @Operation(summary = "Retrieve most recent populations for all servers",
            description = "Returns the most recent populations for all servers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of populations",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PopulationListResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No populations found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/recent",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationListResponse> getAllRecent() {
        var allPopulations = populationService.getAllRecent();
        return ResponseEntity.ok(allPopulations);
    }

    @Operation(summary = "Retrieve populations for a server",
            description = "Returns all populations for the specified server within the given time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server populations",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No populations found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationPageResponse> getForServer(
             @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003",
                    required = true)
            @PathVariable String serverIdentifier,
            @Parameter(description = "Time range in days to retrieve populations for")
            @RequestParam(defaultValue = "7") int timeRange,
            @PageableDefault(size = 100, sort = "updatedAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable) {

        var populationsForServer = populationService.getForServer(serverIdentifier, new TimeRange(timeRange), pageable);
        return ResponseEntity.ok(populationsForServer);
    }

        @Operation(summary = "Retrieve most recent population for a server",
            description = "Returns most recent population for the specified server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server population",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No recent population found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}/recent",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationResponse> getRecentForServer(
             @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003",
                    required = true)
            @PathVariable String serverIdentifier) {

        var priceResponse = populationService.getRecentForServer(serverIdentifier);
        return ResponseEntity.ok(priceResponse);
    }

    @Operation(summary = "Retrieve total population for server",
            description = "Returns the most recent population for server name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server population",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TotalPopResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No populations found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverName}/total",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TotalPopResponse> getTotalForServer(
            @Parameter(description = "Server name for finding total population (case insensitive)",
            example = "everlook",
            required = true)
            @PathVariable String serverName) {
        var totalPopulation = populationService.getTotalPopulation(serverName);
        return ResponseEntity.ok(totalPopulation);
    }

    @Operation(summary = "Retrieve populations for specified search request",
            description = "Returns all populations that match the given search request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of populations for the given search request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search request", content = @Content),
            @ApiResponse(responseCode = "404", description = "No populations found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationPageResponse> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search request for filtering populations",
            required = true)
            @RequestBody @Valid SearchRequest searchRequest,
            @PageableDefault(size = 100, sort = "updatedAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable) {
        var responseForSearch = populationService.search(searchRequest, pageable);
        return ResponseEntity.ok(responseForSearch);
    }

    @Operation(summary = "Retrieve populations for the specified region name",
            description = "Returns recent populations that match the given region name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of populations for the region name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid region name", content = @Content),
            @ApiResponse(responseCode = "404", description = "No populations found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/regions/{regionName}")
    public ResponseEntity<PopulationListResponse> getForRegion(
            @Parameter(description = "Region name to retrieve prices for. Can be either 'eu' or 'us'",
                    example = "eu",
                    required = true)
            @PathVariable String regionName) {
        var populationForRegion = populationService.getRecentForRegion(regionName);
        return ResponseEntity.ok(populationForRegion);
    }
    
    @Operation(summary = "Retrieve populations for the specified faction name",
            description = "Returns recent populations that match the given faction name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of populations for the faction name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid faction name", content = @Content),
            @ApiResponse(responseCode = "404", description = "No populations found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/factions/{factionName}")
    public ResponseEntity<PopulationListResponse> getForFaction(
            @Parameter(description = "Faction name to retrieve prices for. Can be either 'alliance' or 'horde'",
                    example = "alliance",
                    required = true)
            @PathVariable String factionName) {
        var populationForFaction = populationService.getRecentForFaction(factionName);
        return ResponseEntity.ok(populationForFaction);
    }
}
