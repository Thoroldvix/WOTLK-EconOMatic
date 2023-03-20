package com.example.g2gcalculator.repository;

import com.example.g2gcalculator.model.Realm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealmRepository extends JpaRepository<Realm, Integer> {


    @Query("select r from Realm r join fetch r.region join fetch r.auctionHouses")
    List<Realm> findAllFetch();
}