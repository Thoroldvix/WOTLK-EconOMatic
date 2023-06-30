package com.thoroldvix.economatic.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Recommendations API", description = "API for retrieving server recommendations for farming gold")
@RequestMapping("/wow-classic/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;


    @Operation(summary = "Retrieve recommendations for item list",
            description = """
            Returns a list of recommended servers based on the input item list.
             Up to 'limit' servers are returned""", tags = {"Recommendation"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of recommendations",
                            content = @Content(schema = @Schema(implementation = RecommendationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid item list or limit"),
                    @ApiResponse(responseCode = "500", description = "An unexpected exception occurred")
            })
    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendationsForItemList(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Item list to retrieve recommendations for", required = true)
            @RequestBody
            RecommendationRequest request,
            @RequestParam(defaultValue = "5")
            @Parameter(description = "Limit of recommendations to retrieve") int limit,
            @Parameter(description = "Whether to use market value or min buyout for item prices")
            @RequestParam(defaultValue = "true") boolean isMarketValue
    ) {
        var recommendations = recommendationService.getRecommendationsForItemList(request, limit, isMarketValue);
        return ResponseEntity.ok(recommendations);
    }

}
