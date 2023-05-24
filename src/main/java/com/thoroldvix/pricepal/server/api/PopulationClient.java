package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.server.dto.PopulationResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "population", url = "https://api.warcrafttavern.com/armory/server/wotlk/")
public interface PopulationClient {

    @GetMapping("{region}/{server}")
    PopulationResponse getPopulationForServer(@PathVariable Region region, @PathVariable String server);


}
