package com.example.g2gcalculator.repository;

import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassicPriceRepository extends JpaRepository<Price, Integer> {
    @Query("select p from Price p where p.realm = ?1 order by p.updatedAt desc limit 1")
    Optional<Price> findMostRecentPriceByRealm(Realm realm);

    Page<Price> findAllByRealm(Realm realm, Pageable pageable);





}