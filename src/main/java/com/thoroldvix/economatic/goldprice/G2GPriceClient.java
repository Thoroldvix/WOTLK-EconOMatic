package com.thoroldvix.economatic.goldprice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "G2GPriceClient", url = "https://sls.g2g.com/offer/search?service_id=lgc_service_1&brand_id=lgc_game_29076&sort=lowest_price&country=US&page_size=96&currency=USD")
interface G2GPriceClient {

    @GetMapping
    String getAllPrices();
}