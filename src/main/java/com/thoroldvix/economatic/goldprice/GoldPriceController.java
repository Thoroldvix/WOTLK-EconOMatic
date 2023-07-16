package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.common.dto.TimeRange;
import com.thoroldvix.economatic.search.SearchRequest;
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
@Tag(name = "Prices API", description = "API for retrieving server gold prices")
@RequestMapping("/wow-classic/api/v1/servers/prices")
@RequiredArgsConstructor
class GoldPriceController {

    private final GoldPriceService goldPriceServiceImpl;

    @Operation(summary = "Retrieve all prices",
            description = "Returns all prices within the specified time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricePageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPricePageResponse> getAll(
            @Parameter(description = "Time range in days to retrieve populations for",
                    example = "7") @RequestParam(defaultValue = "7")
            int timeRange,
            @ParameterObject
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100)
            Pageable pageable) {
        var prices = goldPriceServiceImpl.getAll(new TimeRange(timeRange), pageable);
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Retrieve most recent prices for all servers",
            description = "Returns the most recent prices for all servers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GoldPriceListResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPriceListResponse> getAllRecent() {
        var prices = goldPriceServiceImpl.getAllRecent();
        return ResponseEntity.ok(prices);
    }

     @Operation(summary = "Retrieve most recent prices for the given server list",
            description = "Returns the most recent prices for the given server list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GoldPriceListResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPriceListResponse> getRecentForServers(@RequestBody @Valid GoldPriceRequest request) {
        var prices = goldPriceServiceImpl.getRecentForServerList(request);
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Retrieve prices for a server",
            description = "Returns all prices for the specified server within the given time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricePageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPricePageResponse> getForServer(
            @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003",
                    required = true)
            @PathVariable String serverIdentifier,
            @Parameter(description = "Time range in days to retrieve populations for",
                    example = "7")
            @RequestParam(defaultValue = "7") int timeRange,
            @ParameterObject @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100) Pageable pageable) {

        var priceResponse = goldPriceServiceImpl.getForServer(serverIdentifier, new TimeRange(timeRange), pageable);
        return ResponseEntity.ok(priceResponse);
    }

    @Operation(summary = "Retrieve most recent price for a server",
            description = "Returns most recent price for the specified server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server price",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricePageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No recent price found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}/recent",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPriceResponse> getRecentForServer(
            @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003",
                    required = true)
            @PathVariable String serverIdentifier) {

        var priceResponse = goldPriceServiceImpl.getRecentForServer(serverIdentifier);
        return ResponseEntity.ok(priceResponse);
    }

    @Operation(summary = "Retrieve prices for specified search request",
            description = "Returns all prices that match the given search request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the given search request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPricePageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search request", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoldPricePageResponse> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search request for filtering prices",
                    required = true)
            @Valid @RequestBody SearchRequest searchRequest,
            @ParameterObject @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100) Pageable pageable) {
        var responseDto = goldPriceServiceImpl.search(searchRequest, pageable);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Retrieve prices for the specified region name",
            description = "Returns recent prices that match the given region name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the region name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid region name", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/regions/{regionName}")
    public ResponseEntity<GoldPriceListResponse> getRecentForRegion(
            @Parameter(description = "Region name to retrieve prices for. Can be either 'eu' or 'us'",
                    example = "eu",
                    required = true)
            @PathVariable String regionName) {

        var prices = goldPriceServiceImpl.getRecentForRegion(regionName);
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Retrieve prices for the specified faction name",
            description = "Returns recent prices that match the given faction name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the faction name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GoldPriceListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid faction name", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/factions/{factionName}")
    public ResponseEntity<GoldPriceListResponse> getRecentForFaction(
            @Parameter(description = "Faction name to retrieve prices for. Can be either 'alliance' or 'horde'",
                    example = "alliance",
                    required = true)
            @PathVariable String factionName) {

        var prices = goldPriceServiceImpl.getRecentForFaction(factionName);
        return ResponseEntity.ok(prices);
    }


}