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

    private final ItemService itemService;

    @Operation(summary = "Retrieves basic item info for all items",
            description = "Returns basic item info for all items", tags = {"Items"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item info",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPagedResponse.class))),
            @ApiResponse(responseCode = "404", description = "No item info found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping
    public ResponseEntity<ItemPagedResponse> getAll(@ParameterObject @PageableDefault(size = 100, sort = "name",
            direction = Sort.Direction.ASC) Pageable pageable) {
        var items = itemService.getAll(pageable);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Retrieves basic item info for a given item identifier",
            description = "Returns basic item info for item identifier", tags = {"Items"})
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
            @Parameter(description = "Identifier of the item can be either item unique name or item ID",
                    example = "righteous-orb or 12811",
                    required = true)
            @PathVariable String itemIdentifier) {
        if (!hasText(itemIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        var item = itemService.getItem(itemIdentifier);
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Retrieves basic item info for a given search criteria",
            description = "Returns basic item info for search criteria. Allows for more fine-grained filtering", tags = {"Items"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item info",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemPagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria", content = @Content),
            @ApiResponse(responseCode = "404", description = "No item info found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<ItemPagedResponse> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search request for filtering items",
                    required = true)
            @RequestBody SearchRequest searchRequest,
            @ParameterObject @PageableDefault(size = 100, sort = "name",
                    direction = Sort.Direction.ASC) Pageable pageable) {
        var searchResult = itemService.search(searchRequest, pageable);
        return ResponseEntity.ok(searchResult);
    }


    @Operation(summary = "Retrieves summary for all items",
            description = "Returns summary of all items. Includes amount of items in each category", tags = {"Items"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of item summary",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemSummaryResponse.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/summary")
    public ResponseEntity<ItemSummaryResponse> getItemSummary() {
        var itemSummaryResponse = itemService.getSummary();
        return ResponseEntity.ok(itemSummaryResponse);
    }

    @Operation(summary = "Adds a new item",
            description = "Adds a new item to the database. Returns the item that was added." +
                    " Make sure to add only items that are tradeable," +
                    " because non-tradeable items aren't used in any other functionality like retrieving item prices or deal," +
                    " which makes them effectively useless", tags = {"Items"})
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
        ItemResponse savedItem = itemService.addItem(itemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @Operation(summary = "Deletes item with given item identifier",
            description = "Deletes item with given item identifier. Returns item that was deleted", tags = {"Items"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful deletion of item",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Item with provided item identifier doesn't exist", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @DeleteMapping("/{itemIdentifier}")
    public ResponseEntity<ItemResponse> deleteItem(
            @Parameter(description = "Identifier of the item can be either item unique name or item ID",
                    example = "righteous-orb or 12811",
                    required = true)
            @PathVariable String itemIdentifier) {
        if (!hasText(itemIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        ItemResponse deletedItem = itemService.deleteItem(itemIdentifier);
        return ResponseEntity.ok(deletedItem);
    }
}