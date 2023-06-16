package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.shared.RequestDto;
import com.thoroldvix.pricepal.shared.StatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Tag(name = "Populations API", description = "API for retrieving server population")
@RequiredArgsConstructor
@RequestMapping("/wow-classic/api/v1/populations")
public class PopulationController {

    private final PopulationService populationService;
    private final PopulationStatsService populationStatsService;

    @Operation(summary = "Retrieves all populations",
            description = "Return all population scans for given time range in days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of populations",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PopulationResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No populations found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PopulationResponse>> getAll(@Parameter(description = "Range of days to retrieve populations for")
                                                                      @RequestParam(defaultValue = "7") int timeRange,
                                                           @ParameterObject Pageable pageable) {
        List<PopulationResponse> allPopulations = populationService.getAll(timeRange, pageable);
        return ResponseEntity.ok(allPopulations);
    }

    @Operation(summary = "Retrieves most recent populations for all servers",
            description = "Returns most recent population scans for all servers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of populations",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PopulationResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No populations found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/recent",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PopulationResponse>> getAllRecent(@ParameterObject Pageable pageable) {
        List<PopulationResponse> allPopulations = populationService.getAllRecent(pageable);
        return ResponseEntity.ok(allPopulations);
    }

    @Operation(summary = "Retrieves populations for given server identifier",
            description = "Returns all population scans for time range and server identifier. Server identifier can be server unique name or server ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Population history retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PopulationResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Incorrect server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "Population not found for server identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/servers/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PopulationResponse>> getForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier,
            @ParameterObject Pageable pageable) {
        List<PopulationResponse> populationForServer = populationService.getForServer(serverIdentifier,  pageable);
        return ResponseEntity.ok(populationForServer);
    }

    @Operation(summary = "Retrieves most recent population for given server identifier",
            description = "Returns most recent population for server identifier. Server identifier can be server unique name or server ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server population",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No populations found for server identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/servers/{serverIdentifier}/recent",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationResponse> getRecentForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier) {
        PopulationResponse population = populationService.getRecentForServer(serverIdentifier);
        return ResponseEntity.ok(population);
    }

    @Operation(summary = "Retrieves populations for specified search criteria",
            description = "Returns all populations that match given search criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of populations for search criteria",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PopulationResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Incorrect search criteria", content = @Content),
            @ApiResponse(responseCode = "404", description = "No populations found for search criteria", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PopulationResponse>> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search criteria for filtering populations. Based on population properties")
            @RequestBody RequestDto requestDto,
            @ParameterObject Pageable pageable) {
        List<PopulationResponse> responseForSearch = populationService.search(requestDto, pageable);
        return ResponseEntity.ok(responseForSearch);
    }

    @Operation(summary = "Retrieves basic population statistics for all servers",
            description = "The statistics are based on all server population scans and the time range in days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StatsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Time range less than 1 day", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for all populations", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/stats",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatsResponse<PopulationResponse>> getStatsForAll(
            @Parameter(description = "Range of days to retrieve statistics for")
            @RequestParam(defaultValue = "7") int timeRange) {
        StatsResponse<PopulationResponse> statsForAll = populationStatsService.getForAll(timeRange);
        return ResponseEntity.ok(statsForAll);
    }

    @Operation(summary = "Retrieves basic population statistics for a server identifier",
            description = "The statistics are based on the provided identifier and the time range in days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StatsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for server identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/stats/servers/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatsResponse<PopulationResponse>> getStatsForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier) {
        StatsResponse<PopulationResponse> statsForServer = populationStatsService.getForServer(serverIdentifier);
        return ResponseEntity.ok(statsForServer);
    }

    @Operation(summary = "Retrieves population of both factions for given server name",
            description = "Returns populations of both factions as well as total population for given server name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of population",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TotalPopResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect server name", content = @Content),
            @ApiResponse(responseCode = "404", description = "No population found for server name", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/servers/{serverName}/total",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TotalPopResponse> getTotalPopulation(
            @Parameter(description = "Server name (e.g. 'Everlook') case insensitive")
            @PathVariable String serverName) {
        TotalPopResponse totalPopulationForServerName = populationService.getTotalPopulation(serverName);
        return ResponseEntity.ok(totalPopulationForServerName);
    }

    @Operation(summary = "Retrieves basic population statistics for a specified search criteria",
            description = "The statistics are based on the provided search criteria and the time range in days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StatsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect search criteria", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for search criteria", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping(value = "/stats/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatsResponse<PopulationResponse>> getStatsForSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search criteria to retrieve statistics for Based on population properties")
            @RequestBody RequestDto requestDto) {

        StatsResponse<PopulationResponse> statsForSearch = populationStatsService.getForSearch(requestDto);
        return ResponseEntity.ok(statsForSearch);
    }


}
