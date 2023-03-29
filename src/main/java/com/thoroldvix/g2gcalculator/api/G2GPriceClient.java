package com.thoroldvix.g2gcalculator.api;

import com.thoroldvix.g2gcalculator.dto.G2GApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "g2gPriceClient", url = "https://sls.g2g.com/offer/search?service_id=lgc_service_1&brand_id=lgc_game_29076&sort=lowest_price&country=US&page_size=48")
public interface G2GPriceClient {

    @GetMapping(produces = "application/json")
    G2GApiResponse getPriceForRealm(@RequestParam("region_id") String regionId,
                                    @RequestParam("currency") String currency);
}