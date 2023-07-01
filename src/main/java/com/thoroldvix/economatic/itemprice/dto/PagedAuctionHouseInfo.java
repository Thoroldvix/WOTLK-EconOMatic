package com.thoroldvix.economatic.itemprice.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;



@Builder
public record PagedAuctionHouseInfo(
        int page,
        int pageSize,
        int totalPages,
        long totalElements,
        @JsonUnwrapped
        AuctionHouseInfo auctionHouseInfo

) {

}
