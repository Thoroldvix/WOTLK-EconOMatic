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
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.thoroldvix.economatic.shared.ValidationUtils.hasText;

@RequestMapping("/wow-classic/api/v1/items/prices")
@RestController
@Tag(name = "Item Prices API", description = "API for retrieving item prices")
@RequiredArgsConstructor
public class ItemPriceController {

    private final ItemPriceService itemPriceService;

    @Operation(summary = "Retrieve recent item prices for a server",
            description = "Returns all recent item prices for the specified server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedAuctionHouseInfo.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No item prices found for the given server identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/servers/{serverIdentifier}")
    public ResponseEntity<PagedAuctionHouseInfo> getRecentForServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier,
            @ParameterObject
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100)
            Pageable pageable) {
        if (!hasText(serverIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        var auctionHouseInfo = itemPriceService.getRecentForServer(serverIdentifier, pageable);
        return ResponseEntity.ok(auctionHouseInfo);
    }

    @Operation(summary = "Retrieve the recent price of an item in a given server",
            description = "Returns the recent price of the specified item in the given server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item price",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuctionHouseInfo.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier or item identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No item price found for the given server and item identifiers", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/servers/{serverIdentifier}/{itemIdentifier}/recent")
    public ResponseEntity<AuctionHouseInfo> getRecentForServerAndItem(
            @Parameter(description = "Identifier of the server can be either server unique name in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier,
            @Parameter(description = "Identifier of the item can be either item unique name (e.g 'righteous-orb') or item ID")
            @PathVariable String itemIdentifier) {
        boolean isInvalidInputs = !hasText(serverIdentifier) || !hasText(itemIdentifier);
        if (isInvalidInputs) {
            return ResponseEntity.badRequest().build();
        }
        var auctionHouseInfo = itemPriceService.getRecentForServer(serverIdentifier, itemIdentifier);
        return ResponseEntity.ok(auctionHouseInfo);
    }

    @Operation(summary = "Retrieve price scans for an item in a server",
            description = "Returns the recent prices of the specified item in the given server within a specified time range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item prices",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedAuctionHouseInfo.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier or item identifier or time range", content = @Content),
            @ApiResponse(responseCode = "404", description = "No item prices found for the given server and item identifiers", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/servers/{serverIdentifier}/{itemIdentifier}")
    public ResponseEntity<PagedAuctionHouseInfo> getRecentForServerAndItem(
            @Parameter(description = "Identifier of the server can be either server unique name in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier,
            @Parameter(description = "Identifier of the item can be either item unique serverName (e.g 'righteous-orb') or item ID")
            @PathVariable String itemIdentifier,
            @Parameter(description = "Time range in days to retrieve populations for")
            @RequestParam(defaultValue = "7") int timeRange,
            @ParameterObject
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100)
            Pageable pageable) {
        boolean isInvalidInputs = !hasText(serverIdentifier) || !hasText(itemIdentifier);
        if (isInvalidInputs) {
            return ResponseEntity.badRequest().build();
        }
        var auctionHouseInfo = itemPriceService.getForServer(serverIdentifier, itemIdentifier, new TimeRange(timeRange), pageable);
        return ResponseEntity.ok(auctionHouseInfo);
    }

    @Operation(summary = "Retrieve prices for specified search request",
            description = "Returns all prices that match the given search request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for search request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPricePagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search request", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for the given search request", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<ItemPricePagedResponse> search(@RequestBody
                                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search request for filtering prices")
                                                         SearchRequest searchRequest,
                                                         @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 100)
                                                         @ParameterObject
                                                         Pageable pageable) {
        var itemPrices = itemPriceService.search(searchRequest, pageable);
        return ResponseEntity.ok(itemPrices);
    }

    @Operation(summary = "Retrieve prices for the specified item and region name",
            description = "Returns recent prices that match the given item and region name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the given region name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuctionHouseInfo.class))),
            @ApiResponse(responseCode = "400", description = "Invalid region name or item identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No prices found for the given region name and item identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/regions/{regionName}/{itemIdentifier}")
    public ResponseEntity<AuctionHouseInfo> getRecentForRegionAndItem(
            @Parameter(description = "Region name to retrieve prices for. Can be either 'eu' or 'us'")
            @PathVariable String regionName,
            @Parameter(description = "Identifier of the item can be either item unique name (e.g 'righteous-orb') or item ID")
            @PathVariable String itemIdentifier) {
        boolean isInvalidInputs = !hasText(regionName) || !hasText(itemIdentifier);
        if (isInvalidInputs) {
            return ResponseEntity.badRequest().build();
        }
        var auctionHouseInfo = itemPriceService.getRecentForRegion(regionName, itemIdentifier);
        return ResponseEntity.ok(auctionHouseInfo);
    }

     @Operation(summary = "Retrieve prices for the specified item and faction name",
             description = "Returns recent prices that match the given item and faction name")
     @ApiResponses(value = {
             @ApiResponse(responseCode = "200", description = "Successful retrieval of prices for the given faction name",
                     content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                             schema = @Schema(implementation = AuctionHouseInfo.class))),
             @ApiResponse(responseCode = "400", description = "Invalid faction name or item identifier", content = @Content),
             @ApiResponse(responseCode = "404", description = "No prices found for the given faction name and item identifier", content = @Content),
             @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
     })
    @GetMapping("/factions/{factionName}/{itemIdentifier}")
    public ResponseEntity<AuctionHouseInfo> getRecentForFactionAndItem(
            @Parameter(description = "Faction name to retrieve prices for. Can be either 'alliance' or 'horde'")
            @PathVariable String factionName,
            @Parameter(description = "Identifier of the item can be either item unique name (e.g 'righteous-orb') or item ID")
            @PathVariable String itemIdentifier) {
        boolean isInvalidInputs = !hasText(factionName) || !hasText(itemIdentifier);
        if (isInvalidInputs) {
            return ResponseEntity.badRequest().build();
        }
        var auctionHouseInfo = itemPriceService.getRecentForFaction(factionName, itemIdentifier);
        return ResponseEntity.ok(auctionHouseInfo);
    }
}
