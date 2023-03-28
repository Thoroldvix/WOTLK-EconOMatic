package com.thoroldvix.g2gcalculator.service;


import com.thoroldvix.g2gcalculator.model.Price;
import com.thoroldvix.g2gcalculator.model.Realm;

public interface ScrapingService {

    Price fetchRealmPrice(Realm realm);


}