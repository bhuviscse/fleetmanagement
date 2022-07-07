package com.fleetmanagement.system.repository;

import com.fleetmanagement.system.entity.PackageBarcodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageBarcodeRepository extends JpaRepository<PackageBarcodeEntity, Integer> {

}