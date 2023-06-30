package com.thoroldvix.economatic.server;

import com.thoroldvix.economatic.shared.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Servers API", description = "API for retrieving server information")
@RequestMapping("/wow-classic/api/v1/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverServiceImpl;

    @Operation(summary = "Retrieve server info for a specified search request",
            description = "Returns all servers that match given search request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server info for the given search request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ServerResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid search request", content = @Content),
            @ApiResponse(responseCode = "404", description = "No servers found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServerResponse>> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search request for filtering servers",
                    required = true)
            @RequestBody
            SearchRequest searchRequest) {
        var searchResult = serverServiceImpl.search(searchRequest);
        return ResponseEntity.ok(searchResult);
    }

    @Operation(summary = "Retrieve server info",
            description = "Returns server info for the given server identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server info for the given server identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ServerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "Server info not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponse> getServer(
            @Parameter(description = "Identifier of the server in the format server-faction or server ID",
                    example = "everlook-alliance or 41003",
                    required = true)
            @PathVariable String serverIdentifier) {

        var server = serverServiceImpl.getServer(serverIdentifier);
        return ResponseEntity.ok(server);
    }

    @Operation(summary = "Retrieves all servers",
            description = "Returns all servers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of all servers",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ServerResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No servers found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServerResponse>> getAll() {
        var all = serverServiceImpl.getAll();
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "Retrieves summary for all servers",
            description = "Returns summary of all servers. Includes amount of servers in each category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server summary",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ServerSummaryResponse.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/summary")
    public ResponseEntity<ServerSummaryResponse> getSummary() {
        var summary = serverServiceImpl.getSummary();
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Retrieves all servers for a specified region",
            description = "Returns all servers for a specified region name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of servers",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ServerSummaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid region name", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/regions/{regionName}")
    public ResponseEntity<List<ServerResponse>> getForRegion(
            @Parameter(description = "Region name to retrieve prices for. Can be either 'eu' or 'us'",
                    example = "eu",
                    required = true)
            @PathVariable String regionName) {
        var servers = serverServiceImpl.getAllForRegion(regionName);
        return ResponseEntity.ok(servers);
    }

    @Operation(summary = "Retrieves all servers for a specified faction",
            description = "Returns all servers for a specified faction name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of servers",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ServerSummaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid faction name", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping("/factions/{factionName}")
    public ResponseEntity<List<ServerResponse>> getForFaction(
            @Parameter(description = "Faction name to retrieve prices for. Can be either 'alliance' or 'horde'",
                    example = "alliance",
                    required = true)
            @PathVariable String factionName) {
        var servers = serverServiceImpl.getAllForFaction(factionName);
        return ResponseEntity.ok(servers);
    }
}