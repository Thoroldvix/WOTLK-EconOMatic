package com.example.g2gcalculator.repository;

import com.example.g2gcalculator.model.AuctionHouse;
import com.example.g2gcalculator.model.Realm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionHouseRepository extends JpaRepository<AuctionHouse, Integer> {

    Optional<AuctionHouse> findByRealm(Realm realm);
}