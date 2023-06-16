package com.thoroldvix.pricepal.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {

    Optional<Item> findByName(String itemName);

    Optional<Item> findByUniqueName(String uniqueName);

    @Query("select i from Item i where i.name like %?1%")
    Page<Item> searchItemsByName(String query, Pageable pageable);

}
