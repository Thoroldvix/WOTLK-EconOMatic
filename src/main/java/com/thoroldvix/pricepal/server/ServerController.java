package com.thoroldvix.pricepal.server;

import com.thoroldvix.pricepal.shared.RequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.thoroldvix.pricepal.shared.ValidationUtils.hasText;

@RestController
@Tag(name = "Servers API", description = "Provides API for retrieving server information")
@RequestMapping("/wow-classic/api/v1/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    @Operation(summary = "Retrieves basic server info for given server identifier",
            description = "Returns basic server info for given server identifier. Server identifier can be server unique name or server ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server info",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ServerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect server identifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "Server not found for identifier", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @GetMapping(value = "/{serverIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerResponse> getServer(
            @Parameter(description = "Identifier of the server in the format 'server-faction' (e.g. 'everlook-alliance') or server ID")
            @PathVariable String serverIdentifier) {
        if (!hasText(serverIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(serverService.getServer(serverIdentifier));
    }

    @Operation(summary = "Retrieves basic server info for a specified search criteria",
            description = "Returns all servers that match given search criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of server info for search criteria",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ServerResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Incorrect search criteria", content = @Content),
            @ApiResponse(responseCode = "500", description = "An unexpected exception occurred", content = @Content)
    })
    @PostMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServerResponse>> searchServers(
            @RequestBody(description = "Search criteria for filtering servers. Based on server properties")
            RequestDto requestDto) {
        return ResponseEntity.ok(serverService.searchServers(requestDto));
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
    public ResponseEntity<List<ServerResponse>> getAllServers() {
        return ResponseEntity.ok(serverService.getAllServers());
    }
}