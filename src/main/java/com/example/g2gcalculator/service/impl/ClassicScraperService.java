package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.service.ScraperService;

public class ClassicScraperService implements ScraperService{

    private  final String url = "https://g2g.com/categories/wow-classic-gold?sort=lowest_price";



    public  PriceResponse getPriceDataForRealm(Realm realm) {
        return null;
    }

}