package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.shared.SearchRequest;
import com.thoroldvix.economatic.shared.TimeRange;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.thoroldvix.economatic.shared.ValidationUtils.hasText;

@RestController
@Tag(name = "Prices API", description = "API for retrieving server gold prices")
@RequestMapping("/wow-classic/api/v1/servers/prices")
@RequiredArgsConstructor
public class GoldPriceController {

    private final GoldPriceService goldPriceService;

    @Operation(summary = "Retrieve all prices",
            description = "Returns all prices within the specified time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricesPagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for the given time range", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPricesPagedResponse> getAll(@Parameter(description = "Time range in days to retrieve populations for")
                                                          @RequestParam(defaultValue = "7") int timeRange,
                                                          @ParameterObject @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100)
                                                          Pageable pageable) {
        var prices = goldPriceService.getAll(new TimeRange(timeRange), pageable);
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Retrieve most recent prices for all servers",
            description = "Returns the most recent prices for all servers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GoldPricesResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPricesResponse> getAllRecent() {
        var prices = goldPriceService.getAllRecent();
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Retrieve prices for a server",
            description = "Returns all prices for the specified server within the given time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricesPagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for the given server identifier and time range", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPricesPagedResponse> getForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier,
            @Parameter(description = "Time range in days to retrieve populations for")
            @RequestParam(defaultValue = "7") int timeRange,
            @ParameterObject @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100) Pageable pageable) {
        if (!hasText(serverIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        var priceResponse = goldPriceService.getAllForServer(serverIdentifier, new TimeRange(timeRange), pageable);
        return ResponseEntity.ok(priceResponse);
    }

    @Operation(summary = "Retrieve most recent price for a server",
            description = "Returns most recent price for the specified server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server price",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricesPagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No recent price found for the given server identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}/recent",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPriceResponse> getRecentForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier) {
        if (!hasText(serverIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        var priceResponse = goldPriceService.getRecentForServer(serverIdentifier);
        return ResponseEntity.ok(priceResponse);
    }

    @Operation(summary = "Retrieve prices for specified search request",
            description = "Returns all prices that match the given search request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the given search request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricesPagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search request", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for the given search request", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPricesPagedResponse> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search request for filtering prices")
            @RequestBody SearchRequest searchRequest,
            @ParameterObject @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100) Pageable pageable) {
        var responseDto = goldPriceService.search(searchRequest, pageable);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Retrieve prices for the specified region name",
            description = "Returns recent prices that match the given region name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the region name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricesResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid region name", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for the given region name", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/regions/{regionName}")
    public ResponseEntity<GoldPricesResponse> getRecentForRegion(
            @Parameter(description = "Region name to retrieve prices for. Can be either 'eu' or 'us'")
            @PathVariable String regionName) {
        if (!hasText(regionName)) {
            return ResponseEntity.badRequest().build();
        }
        var prices = goldPriceService.getRecentForRegion(regionName);
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Retrieve prices for the specified faction name",
            description = "Returns recent prices that match the given faction name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the faction name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricesResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid faction name", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for the given faction name", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/factions/{factionName}")
    public ResponseEntity<GoldPricesResponse> getRecentForFaction(
            @Parameter(description = "Faction name to retrieve prices for. Can be either 'alliance' or 'horde'")
            @PathVariable String factionName) {
        if (!hasText(factionName)) {
            return ResponseEntity.badRequest().build();
        }
        var prices = goldPriceService.getRecentForFaction(factionName);
        return ResponseEntity.ok(prices);
    }


}