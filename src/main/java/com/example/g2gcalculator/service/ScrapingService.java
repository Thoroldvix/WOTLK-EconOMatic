package com.example.g2gcalculator.service;


import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;

public interface ScrapingService {

    Price fetchRealmPrice(Realm realm);


}