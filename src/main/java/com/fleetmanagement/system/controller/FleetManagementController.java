package com.fleetmanagement.system.controller;

import com.fleetmanagement.system.domain.dto.PackageDistributionDTO;
import com.fleetmanagement.system.domain.dto.ResponseDTO;
import com.fleetmanagement.system.service.FleetManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class FleetManagementController {

    private final FleetManagementService fleetManagementService;

    @PostMapping(value = "/distributepackage", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO distributePackage(@RequestBody PackageDistributionDTO packageDistributionDTO) {
        log.info("input received " + packageDistributionDTO);
        return fleetManagementService.updatePackageDistributionDetails(packageDistributionDTO);
    }

}
