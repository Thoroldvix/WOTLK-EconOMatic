package com.example.g2gcalculator.repository;

import com.example.g2gcalculator.model.AuctionHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionHouseRepository extends JpaRepository<AuctionHouse, Integer> {
    List<AuctionHouse> findAllByRealmId(Integer realmId);
}