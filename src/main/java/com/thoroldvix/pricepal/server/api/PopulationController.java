package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.server.service.PopulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wow-classic/api/v1/populations")
public class PopulationController {

    private final PopulationService populationService;


}
