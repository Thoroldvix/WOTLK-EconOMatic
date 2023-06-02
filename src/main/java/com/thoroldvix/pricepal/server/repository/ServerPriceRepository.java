package com.thoroldvix.pricepal.server.repository;

import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerPriceRepository extends JpaRepository<ServerPrice, Long>, JpaSpecificationExecutor<ServerPrice> {
    @Query("select p from ServerPrice p where p.server = ?1 order by p.updatedAt desc limit 1")
    Optional<ServerPrice> findMostRecentPriceByServer(Server server);


    @Query("select sp from ServerPrice sp where sp.server.uniqueName = ?1")
    Page<ServerPrice> findAllForServerUniqueName(String serverName, Pageable pageable);
}