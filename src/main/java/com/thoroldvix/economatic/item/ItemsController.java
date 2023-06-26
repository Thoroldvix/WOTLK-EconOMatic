package com.thoroldvix.economatic.item;

import com.thoroldvix.economatic.shared.SearchRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.thoroldvix.economatic.shared.ValidationUtils.hasText;

@RestController
@Tag(name = "Items API", description = "API for retrieving item information")
@RequestMapping("/wow-classic/api/v1/items")
@Validated
@RequiredArgsConstructor
public class ItemsController {

    private final ItemServiceImpl itemServiceImpl;

    @Operation(summary = "Retrieves basic item info for all items",
            description = "Returns basic item info for all items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item info",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPagedResponse.class))),
            @ApiResponse(responseCode = "404", description = "No items found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping
    public ResponseEntity<ItemPagedResponse> getAll(@ParameterObject @PageableDefault(size = 100, sort = "serverName",
            direction = Sort.Direction.ASC) Pageable pageable) {
        var items = itemServiceImpl.getAll(pageable);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Retrieves basic item info for a given item identifier",
            description = "Returns basic item info for item identifier. Item identifier can be either item unique serverName or item ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item info",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid item identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "No item found for given item identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/{itemIdentifier}")
    public ResponseEntity<ItemResponse> getItem(
            @Parameter(description = "Identifier of the item can be either item unique serverName (e.g 'righteous-orb') or item ID")
            @PathVariable String itemIdentifier) {
        if (!hasText(itemIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        var item = itemServiceImpl.getItem(itemIdentifier);
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Retrieves basic item info for a given search criteria",
            description = "Returns basic item info for search criteria. Allows for more fine-grained filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item info",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria", content = @Content),
            @ApiResponse(responseCode = "404", description = "No item found for given search criteria", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<ItemPagedResponse> search(@RequestBody SearchRequest searchRequest,
                                                    @ParameterObject @PageableDefault(size = 100, sort = "serverName",
                                                           direction = Sort.Direction.ASC) Pageable pageable) {
        var searchResult = itemServiceImpl.search(searchRequest, pageable);
        return ResponseEntity.ok(searchResult);
    }


    @Operation(summary = "Retrieves summary for all items",
            description = "Returns summary of all items. Includes amount of items in each category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item summary",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemSummaryResponse.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/summary")
    public ResponseEntity<ItemSummaryResponse> getItemSummary() {
        var itemSummaryResponse = itemServiceImpl.getSummary();
        return ResponseEntity.ok(itemSummaryResponse);
    }

    @Operation(summary = "Adds a new item",
            description = "Adds a new item to the database. Returns the item that was added." +
                    " Make sure to add only items that are tradeable," +
                    " because non-tradeable items aren't used in any other functionality like retrieving item prices or deals," +
                    " which makes them effectively useless")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid item request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Item with given item identifier already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ItemResponse> addItem(@RequestBody @Valid ItemRequest itemRequest) {
        ItemResponse savedItem = itemServiceImpl.addItem(itemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @Operation(summary = "Deletes item with given item identifier",
            description = "Deletes item with given item identifier. Returns item that was deleted. Item identifier can be either item unique serverName or item ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful deletion of item",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Item with provided item identifier doesn't exist", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @DeleteMapping("/{itemIdentifier}")
    public ResponseEntity<ItemResponse> deleteItem(
            @Parameter(description = "Identifier of the item can be either item unique serverName (e.g 'righteous-orb') or item ID")
            @PathVariable String itemIdentifier) {
        if (!hasText(itemIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        ItemResponse deletedItem = itemServiceImpl.deleteItem(itemIdentifier);
        return ResponseEntity.ok(deletedItem);
    }
}