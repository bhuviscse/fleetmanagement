package com.fleetmanagement.system.repository;

import com.fleetmanagement.system.entity.ShipmentDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentDetailsRepository extends JpaRepository<ShipmentDetailsEntity, Long> {

}