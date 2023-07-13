package com.thoroldvix.economatic.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {
    
    Optional<Item> findByUniqueName(String uniqueName);


}
