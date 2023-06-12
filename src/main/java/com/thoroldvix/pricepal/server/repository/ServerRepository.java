package com.thoroldvix.pricepal.server.repository;

import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Integer>, JpaSpecificationExecutor<Server> {

    Optional<Server> findByUniqueName(String uniqueServerName);

    List<Server> findAllByRegion(Region region);
}