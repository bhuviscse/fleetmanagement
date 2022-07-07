package com.fleetmanagement.system.service;

import com.fleetmanagement.system.domain.dto.PackageDistributionDTO;
import com.fleetmanagement.system.domain.dto.ResponseDTO;
import com.fleetmanagement.system.domain.type.State;
import com.fleetmanagement.system.entity.BarcodeDetailsEntity;
import com.fleetmanagement.system.entity.PackageBarcodeEntity;
import com.fleetmanagement.system.entity.SackBarcodeEntity;
import com.fleetmanagement.system.entity.ShipmentDetailsEntity;
import com.fleetmanagement.system.repository.FleetManagementRepository;
import com.fleetmanagement.system.repository.ShipmentDetailsRepository;
import com.fleetmanagement.system.util.FleetManagementUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FleetManagementService {

    public final FleetManagementRepository fleetManagementRepository;
    public final ShipmentDetailsRepository shipmentDetailsRepository;

    public ResponseDTO updatePackageDistributionDetails(final PackageDistributionDTO packageDistributionDTO) {
        log.info("update package distribution started");
        // Getting package , Sack and Barcode details from DB
        // we can make these 3 calls in parallel too
        final List<PackageBarcodeEntity> packageBarcodeEntity = fleetManagementRepository.getPackageBarcodeDetails();
        final List<SackBarcodeEntity> sackBarcodeEntity = fleetManagementRepository.getSackBarcodeDetails();
        final List<BarcodeDetailsEntity> barcodeDetailsEntity = fleetManagementRepository.findBarCodeDetails();
        // Prepare shipment details
        List<ShipmentDetailsEntity> shipmentDetails = new ArrayList<>();

        // prepare package & sack by delivery point
        final Map<String, List<String>> packageSackBarcodeByDeliveryPoint =
            preparePackageAndSackBarcodeByDeliveryPoint(packageBarcodeEntity, sackBarcodeEntity);
        // sackBarcode key and List of packageBarcode as value
        Map<String, List<String>> sackPackageMappingMap = barcodeDetailsEntity.stream()
            .parallel().collect(Collectors.toConcurrentMap(BarcodeDetailsEntity::getBarcodeSack,
                barcodeDetailsObj -> Collections.singletonList(barcodeDetailsObj.getBarcodePackage()), FleetManagementUtil.merge));

        packageDistributionDTO.getRoute()
            .forEach(inputPackageDistribution -> {

                inputPackageDistribution.getDeliveries()
                    .forEach(inputPackageDistributionObj -> {
                        if (FleetManagementUtil.isValidDistributionPoint.test(packageSackBarcodeByDeliveryPoint,
                            inputPackageDistribution.getDeliveryPoint().toString())) {
                            log.info("updated started for the delivery point " + inputPackageDistribution.getDeliveryPoint());
                            ShipmentDetailsEntity shipmentDetailsEntity = ShipmentDetailsEntity.builder().build();
                            // Update DB records status based on input records (delivery)
                            if (packageSackBarcodeByDeliveryPoint.get(inputPackageDistribution.getDeliveryPoint().toString())
                                .contains(inputPackageDistributionObj.getBarcode())) {
                                shipmentDetailsEntity.setBarcode(inputPackageDistributionObj.getBarcode());
                                prepareShipmentDetailsForDistributionCenters(inputPackageDistribution, shipmentDetailsEntity,
                                    inputPackageDistributionObj, barcodeDetailsEntity);
                            }
                            // Package is in input but with different delivery point than the actual delivery point in DB
                            else if (FleetManagementUtil.isBarcodePresentInDBWithDifferentDeliveryPoint
                                .test(packageSackBarcodeByDeliveryPoint, inputPackageDistributionObj)) {
                                log.error(inputPackageDistributionObj.getBarcode() + " attempted delivery to a wrong location");
                                shipmentDetailsEntity.setBarcode(inputPackageDistributionObj.getBarcode());
                                FleetManagementUtil
                                    .setStatusForPackageAndSack(inputPackageDistributionObj, shipmentDetailsEntity, State.LOADED);
                            }
                            // Package not in DB and loaded in delivery items(from input)
                            else {
                                shipmentDetailsEntity.setBarcode(inputPackageDistributionObj.getBarcode());
                                shipmentDetailsEntity.setStatus(State.LOADED.name());
                            }
                            shipmentDetails.add(shipmentDetailsEntity);
                            log.info("updated started for the delivery point " + inputPackageDistribution.getDeliveryPoint());
                        } else {
                            log.error("Invalid Delivery Point" + inputPackageDistribution.getDeliveryPoint());
                        }
                    });
                // Updating Sack status based on package status

                updateSackStatusBasedOnPackageStatus(sackPackageMappingMap,
                    inputPackageDistribution, shipmentDetails);
                // Already created in DB and not loaded (missing in input data)
                updateNonLoadedPackagesInDB(inputPackageDistribution, packageSackBarcodeByDeliveryPoint, shipmentDetails);

            });


        log.info("shipment details saved in DB " + shipmentDetails);
        shipmentDetailsRepository.saveAll(shipmentDetails);
        log.info("update package distribution ended");
        return FleetManagementUtil.prepareResponse(packageDistributionDTO);
    }

    private void updateNonLoadedPackagesInDB(final PackageDistributionDTO.RouteDTO inputPackageDistribution,
                                             final Map<String, List<String>> packageSackBarcodeByDeliveryPoint,
                                             final List<ShipmentDetailsEntity> shipmentDetails) {
        final List<String> inputBarCodeForDeliveryPoint = inputPackageDistribution.getDeliveries()
            .stream().map(PackageDistributionDTO.RouteDTO.DeliveryItemsDTO::getBarcode)
            .collect(Collectors.toList());
        final List<String> dbBarCodeNotLoadedInput = packageSackBarcodeByDeliveryPoint
            .get(inputPackageDistribution.getDeliveryPoint().toString()).stream()
            .filter(barCode -> !(inputBarCodeForDeliveryPoint.contains(barCode)))
            .collect(Collectors.toList());
        dbBarCodeNotLoadedInput
            .forEach(dbBarCodeNotLoadedInputObj -> {
                // sometimes the package will be loaded in different delivery point.
                // So, for those items the status will remains "Loaded" ( not the status as "created")
                if (FleetManagementUtil.isBarCodeNotPresentInInput.test(shipmentDetails, dbBarCodeNotLoadedInputObj)) {
                    shipmentDetails
                        .add(ShipmentDetailsEntity.builder().barcode(dbBarCodeNotLoadedInputObj).status(State.CREATED.name()).build());
                }
            });
    }

    public void updateSackStatusBasedOnPackageStatus(final Map<String, List<String>>
                                                         sackPackageMappingMap, final PackageDistributionDTO.RouteDTO
                                                         inputPackageDistribution, final List<ShipmentDetailsEntity> shipmentDetails) {
        sackPackageMappingMap.keySet().forEach((sackBarCode) -> {
            if (FleetManagementUtil.isAllPackageUnloaded.test(inputPackageDistribution, sackPackageMappingMap.get(sackBarCode))) {
                shipmentDetails.add(ShipmentDetailsEntity.builder().status(State.UNLOADED.name()).barcode(sackBarCode).build());
            }
        });
    }

    private void prepareShipmentDetailsForDistributionCenters(final PackageDistributionDTO.RouteDTO
                                                                  inputPackageDistribution,
                                                              final ShipmentDetailsEntity shipmentDetailsEntity,
                                                              final PackageDistributionDTO.RouteDTO.DeliveryItemsDTO inputPackageDistributionObj,
                                                              final List<BarcodeDetailsEntity> barcodeDetailsEntity) {
        // Packages of Branch (1)/Distribution Centre(2)
        // can be unloaded and Transfer Centre(3)'s package/sack can also be unloaded
        if (FleetManagementUtil.isNonTransferCentre.test(inputPackageDistribution) ||
            (FleetManagementUtil.isBranch.test(inputPackageDistribution) &&
                FleetManagementUtil.isPackage.test(inputPackageDistributionObj))) {
            FleetManagementUtil.setStatusForPackageAndSack(inputPackageDistributionObj, shipmentDetailsEntity, State.UNLOADED);
        } else {
            // delivery point is 3 and its a sack
            // or packages that are assigned to sack can be unloaded
            if (FleetManagementUtil.isSackOrPackageInSack.test(inputPackageDistributionObj, barcodeDetailsEntity)) {
                FleetManagementUtil.setStatusForPackageAndSack(inputPackageDistributionObj, shipmentDetailsEntity, State.UNLOADED);
            }
            // the remaining packages will remains in Loaded state
            else {
                FleetManagementUtil.setStatusForPackageAndSack(inputPackageDistributionObj, shipmentDetailsEntity, State.LOADED);
            }
        }
    }

    private Map<String, List<String>> preparePackageAndSackBarcodeByDeliveryPoint
        (final List<PackageBarcodeEntity> packageBarcodeEntity, List<SackBarcodeEntity> sackBarcodeEntity) {

        final Map<String, List<String>> packageBarcodeByDeliveryPoint = packageBarcodeEntity.stream()
            .parallel().collect(Collectors.toConcurrentMap(PackageBarcodeEntity::getDeliveryPoint,
                packageBarcodeObj -> Collections.singletonList(packageBarcodeObj.getBarcode()), FleetManagementUtil.merge));

        sackBarcodeEntity.stream()
            .parallel().forEach(sackBarCode -> {
                packageBarcodeByDeliveryPoint.computeIfPresent(sackBarCode.getDeliveryPoint(), (k, v) ->
                {
                    packageBarcodeByDeliveryPoint.get(sackBarCode.getDeliveryPoint()).add(sackBarCode.getBarcode());
                    return packageBarcodeByDeliveryPoint.get(sackBarCode.getDeliveryPoint());
                });
                packageBarcodeByDeliveryPoint
                    .putIfAbsent(sackBarCode.getDeliveryPoint(), Collections.singletonList(sackBarCode.getBarcode()));
            }
        );
        return packageBarcodeByDeliveryPoint;
    }
}
