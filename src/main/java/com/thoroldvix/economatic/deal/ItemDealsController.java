package com.thoroldvix.economatic.deal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Item Deals API", description = "API for retrieving item deal")
@RequestMapping("/wow-classic/api/v1/items/deals")
@RequiredArgsConstructor
class ItemDealsController {

    private final ItemDealsService itemDealsService;

    @Operation(summary = "Retrieve deals for server",
            description = "Returns a list of deals for a specific server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ItemDealsList.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Server not found",
                    content = @Content)
    })
    @GetMapping("/{serverIdentifier}")
    public ResponseEntity<ItemDealsList> getDealsForServer(
            @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    required = true,
                    example = "everlook-alliance or 41003")
            @PathVariable String serverIdentifier,
            @Parameter(description = "The limit of deals to be fetched",
                    example = "5")
            @RequestParam(defaultValue = "5") int limit,
            @Parameter(description = "The minimum quantity of deals required",
                    example = "3")
            @RequestParam(defaultValue = "3") int minQuantity,
            @Parameter(description = "The minimum quality of deals required",
                    example = "0")
            @RequestParam(defaultValue = "0") int minQuality) {

        ItemDealsRequest request = new ItemDealsRequest(serverIdentifier, minQuantity, minQuality, limit);
        var deals = itemDealsService.getDealsForServer(request);
        return ResponseEntity.ok(deals);
    }
}
