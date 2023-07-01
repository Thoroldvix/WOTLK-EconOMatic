package com.thoroldvix.economatic.population.rest;

import com.thoroldvix.economatic.server.model.Region;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "population", url = "https://api.warcrafttavern.com/armory")
public interface WarcraftTavernClient {

    @GetMapping("/server/wotlk/{region}/{server}")
    String getForServerAndRegion(@PathVariable Region region, @PathVariable String server);

    @GetMapping("/servers/wotlk")
    String getAll();
}
