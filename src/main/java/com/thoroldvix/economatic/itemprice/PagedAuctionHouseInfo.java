package com.thoroldvix.economatic.itemprice;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;



@Builder
record PagedAuctionHouseInfo(
        int page,
        int pageSize,
        int totalPages,
        long totalElements,
        @JsonUnwrapped
        AuctionHouseInfo auctionHouseInfo

) {

}
