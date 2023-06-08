package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.common.dto.RequestDto;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.StatsResponse;
import com.thoroldvix.pricepal.server.service.ServerPriceService;
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

import static com.thoroldvix.pricepal.common.util.ValidationUtils.hasText;

@RestController
@Tag(name = "Prices API", description = "API for retrieving server gold prices")
@RequestMapping("/wow-classic/api/v1/prices")
@RequiredArgsConstructor
public class ServerPriceController {

    private final ServerPriceService serverPriceService;

    @Operation(summary = "Retrieves all prices",
            description = "Returns all price scans for given time range in days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ServerPriceResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServerPriceResponse>> getAllPrices(@Parameter(description = "Range of days to retrieve prices for")
                                                                  @RequestParam(defaultValue = "7") int timeRangeInDays,
                                                                  @ParameterObject Pageable pageable) {
        List<ServerPriceResponse> prices = serverPriceService.getAllPrices(timeRangeInDays, pageable);
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Retrieves most recent prices for all servers",
            description = "Returns most recent price scans for all servers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ServerPriceResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @GetMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServerPriceResponse>> getAllRecentPrices(@ParameterObject Pageable pageable) {
        List<ServerPriceResponse> prices = serverPriceService.getAllPricesRecent(pageable);
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Retrieves prices for given server identifier",
            description = "Returns all price scans for time range and server identifier. Server identifier can be server unique name or server ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ServerPriceResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Incorrect server identifier", content = @Content),
            @ApiResponse(responseCode = "400", description = "Time range less than 1 day", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for server identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @GetMapping(value = "/servers/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServerPriceResponse>> getPricesForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier,
            @Parameter(description = "Range of days to retrieve prices for.")
            @RequestParam(defaultValue = "7") int timeRangeInDays,
            @ParameterObject Pageable pageable) {
        if (!hasText(serverIdentifier) || timeRangeInDays < 1) {
            return ResponseEntity.badRequest().build();
        }
        List<ServerPriceResponse> prices = serverPriceService.getPricesForServer(serverIdentifier, timeRangeInDays, pageable);
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Retrieves most recent price for given server identifier",
            description = "Returns most recent price for server identifier. Server identifier can be server unique name or server ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server price",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ServerPriceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for server identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @GetMapping(value = "/servers/{serverIdentifier}/recent",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerPriceResponse> getRecentForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier) {
        if (!hasText(serverIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        ServerPriceResponse price = serverPriceService.getRecentForServer(serverIdentifier);
        return ResponseEntity.ok(price);
    }

    @Operation(summary = "Retrieves prices for specified search criteria",
            description = "Returns all prices that match given search criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for search criteria",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ServerPriceResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Incorrect search criteria", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for search criteria", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @PostMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServerPriceResponse>> searchForPrices(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search criteria for filtering prices. Based on price properties")
            @RequestBody RequestDto requestDto,
            @ParameterObject Pageable pageable) {
        List<ServerPriceResponse> responseDto = serverPriceService.searchForPrices(requestDto, pageable, false);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Retrieves prices for a specified search criteria",
            description = "Returns all prices that match given search criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for search criteria",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ServerPriceResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Incorrect search criteria.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for search criteria.", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred.", content = @Content)
    })
    @PostMapping(value = "/servers/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServerPriceResponse>> searchForPricesInServers(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search criteria for filtering prices. Based on server properties")
            @RequestBody RequestDto requestDto,
            @ParameterObject Pageable pageable) {
        List<ServerPriceResponse> responseDto = serverPriceService.searchForPrices(requestDto, pageable, true);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Retrieves basic price statistics for a specified search criteria",
            description = "The statistics are based on the provided search criteria and the time range in days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StatsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect search criteria", content = @Content),
            @ApiResponse(responseCode = "400", description = "Time range less than 1 day", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for search criteria", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @PostMapping(value = "/servers/search/stats",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatsResponse<ServerPriceResponse>> getStatsForPricesInServers(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search criteria to retrieve statistics for. Based on server properties")
            @RequestBody RequestDto requestDto,
            @Parameter(description = "Range of days to retrieve statistics for")
            @RequestParam(defaultValue = "7") int timeRangeInDays) {
        if (timeRangeInDays < 1) {
            return ResponseEntity.badRequest().build();
        }
        StatsResponse<ServerPriceResponse> statsForSearch = serverPriceService.getStatsForSearch(requestDto, true, timeRangeInDays);
        return ResponseEntity.ok(statsForSearch);
    }

    @Operation(summary = "Retrieves basic price statistics for all servers",
            description = "The statistics are based on all server price scans and the time range in days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StatsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Time range less than 1 day", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for all prices", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @GetMapping(value = "/stats")
    public ResponseEntity<StatsResponse<ServerPriceResponse>> getStatsForAllPrices(
            @Parameter(description = "Range of days to retrieve statistics for")
            @RequestParam(defaultValue = "7") int timeRangeInDays) {
        if (timeRangeInDays < 1) {
            return ResponseEntity.badRequest().build();
        }
        StatsResponse<ServerPriceResponse> statsForAllPrices = serverPriceService.getStatsForAll(timeRangeInDays);
        return ResponseEntity.ok(statsForAllPrices);
    }

    @Operation(summary = "Retrieves basic price statistics for a server identifier",
            description = "The statistics are based on the provided identifier and the time range in days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of  statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StatsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect server identifier", content = @Content),
            @ApiResponse(responseCode = "400", description = "Time range less than 1 day", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for server identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @GetMapping(value = "/servers/{serverIdentifier}/stats",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatsResponse<ServerPriceResponse>> getStatsForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier,
            @Parameter(description = "Range of days to retrieve statistics for")
            @RequestParam(defaultValue = "7") int timeRangeInDays) {
        if (!hasText(serverIdentifier) || timeRangeInDays < 1) {
            return ResponseEntity.badRequest().build();
        }
        StatsResponse<ServerPriceResponse> statsForServer = serverPriceService.getStatsForServer(serverIdentifier, timeRangeInDays);
        return ResponseEntity.ok(statsForServer);
    }

    @Operation(summary = "Retrieves basic price statistics for a specified search criteria",
            description = "The statistics are based on the provided search criteria and the time range in days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of  statistics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StatsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect search criteria", content = @Content),
            @ApiResponse(responseCode = "400", description = "Time range less than 1 day", content = @Content),
            @ApiResponse(responseCode = "404", description = "No statistics found for search criteria", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred", content = @Content)
    })
    @PostMapping(value = "/search/stats",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatsResponse<ServerPriceResponse>> getStatsForSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search criteria to retrieve statistics for. Based on price properties")
            @RequestBody RequestDto requestDto,
            @Parameter(description = "Range of days to retrieve statistics for")
            @RequestParam(defaultValue = "7") int timeRangeInDays) {
        if (timeRangeInDays < 1) {
            return ResponseEntity.badRequest().build();
        }
        StatsResponse<ServerPriceResponse> statsForSearch = serverPriceService.getStatsForSearch(requestDto, false, timeRangeInDays);
        return ResponseEntity.ok(statsForSearch);
    }
}