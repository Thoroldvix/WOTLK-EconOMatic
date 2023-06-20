package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.shared.TimeRange;
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

import static com.thoroldvix.pricepal.shared.ValidationUtils.hasText;

@RestController
@Tag(name = "Gold price statistics API", description = "API for retrieving statistics of gold prices")
@RequestMapping("/wow-classic/api/v1/servers/prices/stats")
@RequiredArgsConstructor
public class GoldPriceStatController {

    private final GoldPriceStatsService goldPriceStatsService;

    @Operation(summary = "Retrieve basic price statistics for all servers",
            description = "Retrieves basic price statistics based on all gold price scans and the specified time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for all prices", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping
    public ResponseEntity<GoldPriceStatResponse> getStatsForAll(
            @Parameter(description = "Range of days to retrieve statistics for")
            @RequestParam(defaultValue = "7") int timeRange) {
        var statsForAllPrices = goldPriceStatsService.getForAll(new TimeRange(timeRange));
        return ResponseEntity.ok(statsForAllPrices);
    }

    @Operation(summary = "Retrieve basic price statistics for a server",
            description = "Retrieves basic price statistics based on the provided server identifier and time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for the given server identifier and time range", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPriceStatResponse> getStatsForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier,
            @Parameter(description = "Time range in days to retrieve statistics for")
            @RequestParam(defaultValue = "7") int timeRange) {

        if (!hasText(serverIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        var statsForServer = goldPriceStatsService.getForServer(serverIdentifier, new TimeRange(timeRange));
        return ResponseEntity.ok(statsForServer);
    }

    @Operation(summary = "Retrieve basic price statistics for a region",
            description = "Retrieves basic price statistics based on the provided region serverName and time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid region name or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for the given region name or time range", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/regions/{regionName}")
    public ResponseEntity<GoldPriceStatResponse> getStatsForRegion(@PathVariable String regionName,
                                                                   @Parameter(description = "Time range in days to retrieve statistics for")
                                                                   @RequestParam(defaultValue = "7") int timeRange) {
        if (!hasText(regionName)) {
            return ResponseEntity.badRequest().build();
        }
        var statsForRegion = goldPriceStatsService.getForRegion(regionName, new TimeRange(timeRange));
        return ResponseEntity.ok(statsForRegion);
    }

    @Operation(summary = "Retrieve basic price statistics for a faction",
            description = "Retrieves basic price statistics based on the provided faction serverName and time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceStatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid faction name or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for the given faction name or time range", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/factions/{factionName}")
    public ResponseEntity<GoldPriceStatResponse> getStatsForFaction(@PathVariable String factionName,
                                                                    @Parameter(description = "Time range in days to retrieve statistics for")
                                                                    @RequestParam(defaultValue = "7") int timeRange) {
        if (!hasText(factionName)) {
            return ResponseEntity.badRequest().build();
        }
        var statsForFaction = goldPriceStatsService.getForFaction(factionName, new TimeRange(timeRange));
        return ResponseEntity.ok(statsForFaction);
    }
}
