package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.shared.SearchRequest;
import com.thoroldvix.economatic.shared.TimeRange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RequestMapping("/wow-classic/api/v1/items/prices")
@RestController
@Validated
@Tag(name = "Item Prices API", description = "API for retrieving item prices")
@RequiredArgsConstructor
class ItemPriceController {

    private final ItemPriceService itemPriceServiceImpl;

    @Operation(summary = "Retrieve recent item prices for a server",
            description = "Returns all recent item prices for the specified server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPricePageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/servers/{serverIdentifier}")
    public ResponseEntity<ItemPricePageResponse> getRecentForServer(
            @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003",
                    required = true)
            @PathVariable String serverIdentifier,
            @ParameterObject
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100)
            Pageable pageable) {

        var auctionHouseInfo = itemPriceServiceImpl.getRecentForServer(serverIdentifier, pageable);
        return ResponseEntity.ok(auctionHouseInfo);
    }

    @Operation(summary = "Retrieve the recent price of an item in a given server",
            description = "Returns the recent price of the specified item in the given server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item price",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPriceListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier or item identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No price found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/servers/{serverIdentifier}/{itemIdentifier}/recent")
    public ResponseEntity<ItemPriceListResponse> getRecentForServerAndItem(
            @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003",
                    required = true)
            @PathVariable String serverIdentifier,
            @Parameter(description = "Identifier of the item can be either item unique name or item ID",
                    example = "righteous-orb or 12811",
                    required = true)
            @PathVariable String itemIdentifier) {

        var auctionHouseInfo = itemPriceServiceImpl.getRecentForServer(serverIdentifier, itemIdentifier);
        return ResponseEntity.ok(auctionHouseInfo);
    }

    @Operation(summary = "Retrieve price scans for an item in a server",
            description = "Returns the recent prices of the specified item in the given server within a specified time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPricePageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier or item identifier or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/servers/{serverIdentifier}/{itemIdentifier}")
    public ResponseEntity<ItemPricePageResponse> getRecentForServerAndItem(
            @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003",
                    required = true)
            @PathVariable String serverIdentifier,
            @Parameter(description = "Identifier of the item can be either item unique name or item ID",
                    example = "righteous-orb or 12811",
                    required = true)
            @PathVariable String itemIdentifier,
            @Parameter(description = "Time range in days to retrieve populations for",
                    example = "7")
            @RequestParam(defaultValue = "7") int timeRange,
            @ParameterObject
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100)
            Pageable pageable) {

        var auctionHouseInfo = itemPriceServiceImpl.getForServer(serverIdentifier, itemIdentifier, new TimeRange(timeRange), pageable);
        return ResponseEntity.ok(auctionHouseInfo);
    }

    @Operation(summary = "Retrieve prices for specified search request",
            description = "Returns all prices that match the given search request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for search request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPricePageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search request", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<ItemPricePageResponse> search(@RequestBody
                                                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search request for filtering prices")
                                                        @Valid
                                                        SearchRequest searchRequest,
                                                        @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100)
                                                        @ParameterObject
                                                        Pageable pageable) {
        var itemPrices = itemPriceServiceImpl.search(searchRequest, pageable);
        return ResponseEntity.ok(itemPrices);
    }

    @Operation(summary = "Retrieve recent prices for given item list and server list",
            description = """
                         If no specific servers are specified in the item list request,
                          this function will return the most recent prices from all servers for the given items.
                           However, if a list of servers is provided, the function will return the most recent prices from only those specific servers for the given items
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of recent prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPricePageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid item list", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping("/recent")
    public ResponseEntity<ItemPricePageResponse> getRecentForItemList(@RequestBody @Valid ItemPriceRequest itemList,
                                                                      @PageableDefault(sort = "updated_at", direction = Sort.Direction.DESC, size = 100)
                                                                      @ParameterObject Pageable pageable) {
        var itemPrices = itemPriceServiceImpl.getRecentForItemListAndServers(itemList, pageable);
        return ResponseEntity.ok(itemPrices);
    }

    @Operation(summary = "Retrieve prices for the specified item and region name",
            description = "Returns recent prices that match the given item and region name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the given region name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPriceListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid region name or item identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/regions/{regionName}/{itemIdentifier}")
    public ResponseEntity<ItemPriceListResponse> getRecentForRegionAndItem(
            @Parameter(description = "Region name to retrieve prices for. Can be either 'eu' or 'us'",
                    example = "eu",
                    required = true)
            @PathVariable String regionName,
            @Parameter(description = "Identifier of the item can be either item unique name or item ID",
                    example = "righteous-orb or 12811",
                    required = true)
            @PathVariable String itemIdentifier) {

        var auctionHouseInfo = itemPriceServiceImpl.getRecentForRegion(regionName, itemIdentifier);
        return ResponseEntity.ok(auctionHouseInfo);
    }

    @Operation(summary = "Retrieve prices for the specified item and faction name",
            description = "Returns recent prices that match the given item and faction name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the given faction name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPriceListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid faction name or item identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/factions/{factionName}/{itemIdentifier}")
    public ResponseEntity<ItemPriceListResponse> getRecentForFactionAndItem(
            @Parameter(description = "Faction name to retrieve prices for. Can be either 'alliance' or 'horde'",
                    example = "horde",
                    required = true)
            @PathVariable String factionName,
            @Parameter(description = "Identifier of the item can be either item unique name or item ID",
                    example = "righteous-orb or 12811",
                    required = true)
            @PathVariable String itemIdentifier) {

        var auctionHouseInfo = itemPriceServiceImpl.getRecentForFaction(factionName, itemIdentifier);
        return ResponseEntity.ok(auctionHouseInfo);
    }
}
