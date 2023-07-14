package com.thoroldvix.economatic.stats.goldprice;

import com.thoroldvix.economatic.dto.TimeRange;
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
@Tag(name = "Gold Price Stats API", description = "API for retrieving gold price statistics")
@RequestMapping("/wow-classic/api/v1/servers/prices/stats")
@RequiredArgsConstructor
class GoldPriceStatController {

    private final GoldPriceStatService goldPriceStatServiceImpl;

    @Operation(summary = "Retrieve basic price statistics for all servers",
            description = "Retrieves basic price statistics based on all gold price scans and the specified time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping
    public ResponseEntity<GoldPriceStatResponse> getStatsForAll(
            @Parameter(description = "Range of days to retrieve statistics for",
                    example = "7")
            @RequestParam(defaultValue = "7") int timeRange) {
        var statsForAllPrices = goldPriceStatServiceImpl.getForAll(new TimeRange(timeRange));
        return ResponseEntity.ok(statsForAllPrices);
    }

    @Operation(summary = "Retrieve basic price statistics for a server",
            description = "Retrieves basic price statistics based on the provided server identifier and time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPriceStatResponse> getStatsForServer(
            @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003")
            @PathVariable String serverIdentifier,
            @Parameter(description = "Time range in days to retrieve statistics for",
                    example = "7")
            @RequestParam(defaultValue = "7") int timeRange) {

        var statsForServer = goldPriceStatServiceImpl.getForServer(serverIdentifier, new TimeRange(timeRange));
        return ResponseEntity.ok(statsForServer);
    }

    @Operation(summary = "Retrieve basic price statistics for a region",
            description = "Retrieves basic price statistics based on the provided region name and time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid region name or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/regions/{regionName}")
    public ResponseEntity<GoldPriceStatResponse> getStatsForRegion(
            @Parameter(description = "Region name to retrieve statistics for. Can be either 'eu' or 'us'",
                    example = "eu",
                    required = true)
            @PathVariable String regionName,
            @Parameter(description = "Time range in days to retrieve statistics for",
                    example = "7")
            @RequestParam(defaultValue = "7") int timeRange) {

        var statsForRegion = goldPriceStatServiceImpl.getForRegion(regionName, new TimeRange(timeRange));
        return ResponseEntity.ok(statsForRegion);
    }

    @Operation(summary = "Retrieve basic price statistics for a faction",
            description = "Retrieves basic price statistics based on the provided faction name and time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid faction name or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/factions/{factionName}")
    public ResponseEntity<GoldPriceStatResponse> getStatsForFaction(
            @Parameter(description = "Faction name to retrieve prices for. Can be either 'alliance' or 'horde'",
                    example = "alliance",
                    required = true)
            @PathVariable String factionName,
            @Parameter(description = "Time range in days to retrieve statistics for",
                    example = "7")
            @RequestParam(defaultValue = "7") int timeRange) {

        var statsForFaction = goldPriceStatServiceImpl.getForFaction(factionName, new TimeRange(timeRange));
        return ResponseEntity.ok(statsForFaction);
    }
}
