package com.fleetmanagement.system.repository;

import com.fleetmanagement.system.entity.BarcodeDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarcodeDetailsRepository extends JpaRepository<BarcodeDetailsEntity, Integer> {

}