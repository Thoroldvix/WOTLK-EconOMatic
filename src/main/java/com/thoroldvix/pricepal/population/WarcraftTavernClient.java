package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.Region;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "population", url = "https://api.warcrafttavern.com/armory/server/wotlk/")
public interface WarcraftTavernClient {

    @GetMapping("{region}/{server}")
    Map<String, String> getPopulationForServer(@PathVariable Region region, @PathVariable String server);
}
