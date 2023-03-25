package com.example.g2gcalculator.util;

import com.example.g2gcalculator.dto.AuctionHouseResponse;
import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestUtil {

    private TestUtil() {}

    public static Price createPrice() {
        return Price.builder()
                .id(1)
                .price(BigDecimal.valueOf(100))
                .realm(createRealm())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static PriceResponse createPriceResponse() {
        return PriceResponse.builder()
                .price("100/1k")
                .realmName("test")
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Price createPrice(BigDecimal price, Realm realm) {
        Price result = createPrice();
        result.setPrice(price);
        result.setRealm(realm);
        return result;
    }
     public static Price createPrice(int id, BigDecimal price, LocalDateTime updatedAt, Realm realm) {
        return Price.builder()
                .id(id)
                .realm(realm)
                .price(price)
                .updatedAt(updatedAt)
                .build();
    }
    public static Price createPrice(BigDecimal price) {
        return createPrice(1, price, LocalDateTime.now(), createRealm());
    }

    public static List<Price> createPriceList(int amount) {
        return IntStream.rangeClosed(1, amount)
                .mapToObj(i -> createPrice(BigDecimal.valueOf(i)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static String getFullRealmName(Realm realm) {
        return (realm.getFaction() == null) ? realm.getName() : realm.getName() + "-" + realm.getFaction();
    }



    public static PriceResponse createPriceResponse(BigDecimal price, LocalDateTime updatedAt, Realm realm) {
        return PriceResponse.builder()
                .price(price + "/1k")
                .realmName(getFullRealmName(realm))
                .updatedAt(updatedAt)
                .build();
    }

    public static PriceResponse createPriceResponse(BigDecimal price) {
        return createPriceResponse(price, LocalDateTime.now(), createRealm());
    }


    public static List<PriceResponse> createPriceResponseList(int amount) {
        return IntStream.rangeClosed(1, amount)
                .mapToObj(i -> createPriceResponse(BigDecimal.valueOf(i)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static Realm createRealm() {
        return Realm.builder()
                .id(1)
                .name("test")
                .prices(new ArrayList<>())
                .gameVersion(GameVersion.CLASSIC)
                .region(Region.EU)
                .faction(Faction.ALLIANCE)
                .build();
    }


    public static Realm createRealm(int id) {
         Realm realm = createRealm();
         realm.setId(id);
         return realm;
    }

    public static List<Realm> createRealmList(int amount) {
        return IntStream.rangeClosed(1, amount)
                .mapToObj(TestUtil::createRealm)
                .collect(Collectors.toList());
    }

    public static RealmResponse createRealmResponse(int id, String name, Faction faction, GameVersion gameVersion) {
        return RealmResponse.builder()
                .id(id)
                .name(name)
                .region("EU")
                .gameVersion(gameVersion)
                .auctionHouse(new AuctionHouseResponse(1))
                .faction(faction)
                .build();
    }

    public static RealmResponse createRealmResponse(int id) {
        return RealmResponse.builder()
                .id(id)
                .name("test")
                .region("EU")
                .gameVersion(GameVersion.CLASSIC)
                .auctionHouse(new AuctionHouseResponse(1))
                .faction(Faction.ALLIANCE)
                .build();
    }




    public static List<RealmResponse> createRealmResponseList(int amount) {
        return IntStream.rangeClosed(1, amount)
                .mapToObj(TestUtil::createRealmResponse)
                .collect(Collectors.toList());
    }
}