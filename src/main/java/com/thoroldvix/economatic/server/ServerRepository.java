package com.thoroldvix.economatic.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface ServerRepository extends JpaRepository<Server, Integer>, JpaSpecificationExecutor<Server> {

    Optional<Server> findByUniqueName(String uniqueName);

    List<Server> findAllByRegion(Region region);

    List<Server> findAllByFaction(Faction faction);
}