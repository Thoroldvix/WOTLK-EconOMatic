package com.example.g2gcalculator.service;


import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Realm;

public interface ScraperService {

    PriceResponse getPriceForRealm(Realm realm);
}
//service_id=lgc_service_1&brand_id=lgc_game_29076&region_id=ac3f85c1-7562-437e-b125-e89576b9a38e&fa=lgc_29076_server%3Algc_29076_server_41023&sort=lowest_price&include_offline=1