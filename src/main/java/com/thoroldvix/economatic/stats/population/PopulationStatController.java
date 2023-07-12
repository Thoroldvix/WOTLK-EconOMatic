package com.thoroldvix.economatic.stats.population;

import com.thoroldvix.economatic.shared.dto.TimeRange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Populations Stats API", description = "API for retrieving population stats")
@RequiredArgsConstructor
@RequestMapping("/wow-classic/api/v1/servers/populations/stats")
public class PopulationStatController {

    private final PopulationStatsService populationStatsService;

    @Operation(summary = "Retrieves basic population statistics for all servers",
            description = "The statistics are based on all server population scans and the time range in days", tags = {"Population stats"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PopulationStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Time range less than 1 day", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationStatResponse> getStatsForAll(
            @Parameter(description = "Range of days to retrieve statistics for",
                    example = "7")
            @RequestParam(defaultValue = "7") int timeRange) {

        var statsForAll = populationStatsService.getForAll(new TimeRange(timeRange));
        return ResponseEntity.ok(statsForAll);
    }

    @Operation(summary = "Retrieves basic population statistics for a server identifier",
            description = "The statistics are based on the provided identifier and the time range in days", tags = {"Population stats"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationStatResponse> getStatsForServer(
            @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003",
                    required = true)
            @PathVariable String serverIdentifier,
            @Parameter(description = "Range of days to retrieve statistics for",
                    example = "7")
            @RequestParam(defaultValue = "7") int timeRange) {

        var statsForServer = populationStatsService.getForServer(serverIdentifier, new TimeRange(timeRange));
        return ResponseEntity.ok(statsForServer);
    }

    @Operation(summary = "Retrieve basic population statistics for a region",
            description = "Retrieves basic population statistics based on the provided region name and time range", tags = {"Population stats"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid region name or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/regions/{regionName}")
    public ResponseEntity<PopulationStatResponse> getStatsForRegion(
            @Parameter(description = "Region name to retrieve prices for. Can be either 'eu' or 'us'",
                    example = "eu",
                    required = true)
            @PathVariable String regionName,
            @Parameter(description = "Range of days to retrieve statistics for",
            example = "7")
            @RequestParam(defaultValue = "7") int timeRange) {

        var statsForRegion = populationStatsService.getForRegion(regionName, new TimeRange(timeRange));
        return ResponseEntity.ok(statsForRegion);
    }

    @Operation(summary = "Retrieve basic population statistics for a faction",
            description = "Retrieves basic population statistics based on the provided faction name and time range", tags = {"Population stats"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PopulationStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid faction name or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/factions/{factionName}")
    public ResponseEntity<PopulationStatResponse> getStatsForFaction(
            @Parameter(description = "Faction name to retrieve prices for. Can be either 'alliance' or 'horde'",
                    example = "alliance",
                    required = true)
            @PathVariable
            String factionName,
            @Parameter(description = "Range of days to retrieve statistics for",
                    example = "7")
            @RequestParam(defaultValue = "7") int timeRange) {

        var statsForFaction = populationStatsService.getForFaction(factionName, new TimeRange(timeRange));
        return ResponseEntity.ok(statsForFaction);
    }
}
