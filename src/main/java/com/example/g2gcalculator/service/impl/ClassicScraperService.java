package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.service.ScraperService;

public class ClassicScraperService implements ScraperService {

    private final String rootUrl = "https://g2g.com/categories";
    private final String wowClassicGoldCategory = "wow-classic-gold";



    @Override
    public PriceResponse getPriceForRealm(Realm realm) {
        return null;
    }
}