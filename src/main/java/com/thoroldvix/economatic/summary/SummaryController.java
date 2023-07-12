package com.thoroldvix.economatic.summary;

import com.thoroldvix.economatic.summary.item.ItemSummaryResponse;
import com.thoroldvix.economatic.summary.item.ItemSummaryService;
import com.thoroldvix.economatic.summary.server.ServerSummaryResponse;
import com.thoroldvix.economatic.summary.server.ServerSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wow-classic/api/v1/summary")
@Tag(name = "Summary API", description = "API for retrieving summary of items and servers")
@RequiredArgsConstructor
public class SummaryController {

    private final ItemSummaryService itemSummaryService;
    private final ServerSummaryService serverSummaryService;

    @Operation(summary = "Retrieves summary for all items",
            description = "Returns summary of all items. Includes amount of items in each category", tags = {"Summary API", "Items API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item summary",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemSummaryResponse.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/items")
    public ResponseEntity<ItemSummaryResponse> getItemSummary() {
        var itemSummaryResponse = itemSummaryService.getSummary();
        return ResponseEntity.ok(itemSummaryResponse);
    }

    @Operation(summary = "Retrieves summary for all servers",
            description = "Returns summary of all servers. Includes amount of servers in each category", tags = {"Summary API", "Servers API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server summary",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ServerSummaryResponse.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/servers")
    public ResponseEntity<ServerSummaryResponse> getSummary() {
        var summary = serverSummaryService.getSummary();
        return ResponseEntity.ok(summary);
    }
}
