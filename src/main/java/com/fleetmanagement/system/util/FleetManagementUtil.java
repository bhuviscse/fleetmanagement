package com.fleetmanagement.system.util;

import com.fleetmanagement.system.domain.dto.PackageDistributionDTO;
import com.fleetmanagement.system.domain.dto.ResponseDTO;
import com.fleetmanagement.system.domain.type.State;
import com.fleetmanagement.system.entity.BarcodeDetailsEntity;
import com.fleetmanagement.system.entity.ShipmentDetailsEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Slf4j
public class FleetManagementUtil {
    public static final String SACK = "C";
    public static final String PACKAGE = "P";
    public static final int TRANSFER_CENTER = 3;
    public static final int BRANCH = 1;

    public static final BinaryOperator<List<String>> merge = (old, latest) ->
        Stream.of(old, latest).flatMap(Collection::stream).collect(Collectors.toList());

    public static final BiPredicate<Map<String, List<String>>, String> isValidDistributionPoint =
        Map::containsKey;

    public static final BiPredicate<PackageDistributionDTO.RouteDTO.DeliveryItemsDTO, List<BarcodeDetailsEntity>>
        isSackOrPackageInSack =
        (inputPackageDistributionObj, barcodeSackPackage) -> inputPackageDistributionObj.getBarcode().startsWith(SACK)
            || barcodeSackPackage.stream()
            .anyMatch(barcodeSackPackageObj -> barcodeSackPackageObj.getBarcodePackage()
                .equalsIgnoreCase(inputPackageDistributionObj.getBarcode()));

    public static final BiPredicate<PackageDistributionDTO.RouteDTO, List<String>> isAllPackageUnloaded =
        (inputPackageDistribution, packageListForSack) -> {
            List<PackageDistributionDTO.RouteDTO.DeliveryItemsDTO> deliveryItemsDTOS = inputPackageDistribution.getDeliveries()
                .stream()
                .filter(barCodeInInput -> packageListForSack.contains(barCodeInInput.getBarcode())).collect(Collectors.toList());
            return !CollectionUtils.isEmpty(deliveryItemsDTOS) && deliveryItemsDTOS.size() == packageListForSack.size()
                && deliveryItemsDTOS.stream()
                .allMatch(delivery -> State.UNLOADED.equals(delivery.getState()));
        };

    // if delivery point is not 3 , we can unload packages
    public static final Predicate<PackageDistributionDTO.RouteDTO> isNonTransferCentre =
        (inputPackageDistribution) -> inputPackageDistribution.getDeliveryPoint() != TRANSFER_CENTER;

    public static final Predicate<PackageDistributionDTO.RouteDTO> isBranch =
        (inputPackageDistribution) -> inputPackageDistribution.getDeliveryPoint() == BRANCH;

    public static final Predicate<PackageDistributionDTO.RouteDTO.DeliveryItemsDTO> isPackage =
        (inputPackageDistributionObj) -> inputPackageDistributionObj.getBarcode().startsWith(PACKAGE);

    public static final BiPredicate<Map<String, List<String>>, PackageDistributionDTO.RouteDTO.DeliveryItemsDTO>
        isBarcodePresentInDBWithDifferentDeliveryPoint =
        (packageSackBarcodeByDeliveryPoint, inputPackageDistributionObj) -> packageSackBarcodeByDeliveryPoint.values().stream()
            .anyMatch(packageSackBarcode -> packageSackBarcode.contains(inputPackageDistributionObj.getBarcode()));

    public static final BiPredicate<List<ShipmentDetailsEntity>, String> isBarCodeNotPresentInInput =
        (shipmentDetails, dbBarCodeNotLoadedInputObj) -> shipmentDetails.stream()
            .noneMatch(
                shipmentDetailsEntity -> shipmentDetailsEntity.getBarcode()
                    .equalsIgnoreCase(dbBarCodeNotLoadedInputObj));

    public static void setStatusForPackageAndSack(final PackageDistributionDTO.RouteDTO.DeliveryItemsDTO inputPackageDistributionObj,
                                                  final ShipmentDetailsEntity shipmentDetailsEntity, final State state) {
        inputPackageDistributionObj.setState(state);
        shipmentDetailsEntity.setStatus(state.name());
    }

    public static ResponseDTO prepareResponse(final PackageDistributionDTO packageDistributionDTO) {
        return ResponseDTO.builder().packageDistribution(packageDistributionDTO).build();
    }

}
