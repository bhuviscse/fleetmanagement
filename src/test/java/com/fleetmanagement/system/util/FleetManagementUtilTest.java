package com.fleetmanagement.system.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fleetmanagement.system.domain.dto.PackageDistributionDTO;
import com.fleetmanagement.system.domain.dto.ResponseDTO;
import com.fleetmanagement.system.domain.type.State;
import com.fleetmanagement.system.entity.BarcodeDetailsEntity;
import com.fleetmanagement.system.entity.ShipmentDetailsEntity;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class FleetManagementUtilTest {

    @Test
    public void prepareResponseTest() {
        ResponseDTO response = FleetManagementUtil.prepareResponse(PackageDistributionDTO.builder().build());
        assertNotNull(response.getPackageDistribution());
    }

    @Test
    public void setStatusForPackageAndSackTest() {
        PackageDistributionDTO.RouteDTO.DeliveryItemsDTO deliveryItemsDTO =
            PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder().build();
        ShipmentDetailsEntity shipmentDetailsEntity = ShipmentDetailsEntity.builder().build();
        FleetManagementUtil
            .setStatusForPackageAndSack(deliveryItemsDTO
                , shipmentDetailsEntity, State.UNLOADED);
        assertEquals(shipmentDetailsEntity.getStatus(), State.UNLOADED.name());
        assertEquals(deliveryItemsDTO.getState().name(), State.UNLOADED.name());
    }

    @Test
    public void barCodeNotPresentInInputSuccessTest() {
        assertTrue(FleetManagementUtil
            .isBarCodeNotPresentInInput.test(Arrays.asList(ShipmentDetailsEntity.builder().barcode("P798800000").build()),
                "P7988000121"));
    }

    @Test
    public void barCodeNotPresentInInputFailureTest() {
        assertFalse(FleetManagementUtil
            .isBarCodeNotPresentInInput.test(Arrays.asList(ShipmentDetailsEntity.builder().barcode("P7988000121").build()),
                "P7988000121"));
    }

    @Test
    public void BarcodePresentInDBWithDifferentDeliveryPointSuccessTest() {
        Map<String, List<String>> packageSackBarcodeByDeliveryPoint = new HashMap<>();
        packageSackBarcodeByDeliveryPoint.put("3", Arrays.asList("P8988000123"));
        PackageDistributionDTO.RouteDTO.DeliveryItemsDTO deliveryItemsDTO =
            PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder().barcode("P8988000123").build();
        assertTrue(FleetManagementUtil
            .isBarcodePresentInDBWithDifferentDeliveryPoint
            .test(packageSackBarcodeByDeliveryPoint, deliveryItemsDTO));
    }

    @Test
    public void BarcodePresentInDBWithDifferentDeliveryPointFailureTest() {
        Map<String, List<String>> packageSackBarcodeByDeliveryPoint = new HashMap<>();
        packageSackBarcodeByDeliveryPoint.put("3", Arrays.asList("P8988000123"));
        PackageDistributionDTO.RouteDTO.DeliveryItemsDTO deliveryItemsDTO =
            PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder().barcode("P8988000122").build();
        assertFalse(FleetManagementUtil
            .isBarcodePresentInDBWithDifferentDeliveryPoint
            .test(packageSackBarcodeByDeliveryPoint, deliveryItemsDTO));
    }

    @Test
    public void isPackageSuccessTest() {
        PackageDistributionDTO.RouteDTO.DeliveryItemsDTO deliveryItemsDTO =
            PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder().barcode("P8988000122").build();
        assertTrue(FleetManagementUtil
            .isPackage
            .test(deliveryItemsDTO));
    }

    @Test
    public void isPackageFailureTest() {
        PackageDistributionDTO.RouteDTO.DeliveryItemsDTO deliveryItemsDTO =
            PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder().barcode("C725799").build();
        assertFalse(FleetManagementUtil
            .isPackage
            .test(deliveryItemsDTO));
    }

    @Test
    public void isBranchSuccessTest() {
        PackageDistributionDTO.RouteDTO routeDTO =
            PackageDistributionDTO.RouteDTO.builder().deliveryPoint(1).build();
        assertTrue(FleetManagementUtil
            .isBranch
            .test(routeDTO));
    }

    @Test
    public void isBranchFailureTest() {
        PackageDistributionDTO.RouteDTO routeDTO =
            PackageDistributionDTO.RouteDTO.builder().deliveryPoint(2).build();
        assertFalse(FleetManagementUtil
            .isBranch
            .test(routeDTO));
    }

    @Test
    public void isTransferCenterSuccessTest() {
        PackageDistributionDTO.RouteDTO routeDTO =
            PackageDistributionDTO.RouteDTO.builder().deliveryPoint(1).build();
        assertTrue(FleetManagementUtil
            .isNonTransferCentre
            .test(routeDTO));
    }

    @Test
    public void isTransferCenterFailureTest() {
        PackageDistributionDTO.RouteDTO routeDTO =
            PackageDistributionDTO.RouteDTO.builder().deliveryPoint(3).build();
        assertFalse(FleetManagementUtil
            .isNonTransferCentre
            .test(routeDTO));
    }

    @Test
    public void isAllPackageUnloadedSuccessTest() {
        PackageDistributionDTO.RouteDTO routeDTO =
            PackageDistributionDTO.RouteDTO.builder().deliveries(Arrays.asList(PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder()
                .barcode("P7988000121").state(State.UNLOADED).build(), PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder()
                .barcode("P7988000122").state(State.UNLOADED).build(), PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder()
                .barcode("P7988000100").state(State.UNLOADED).build())).deliveryPoint(3).build();
        assertTrue(FleetManagementUtil
            .isAllPackageUnloaded
            .test(routeDTO, Arrays.asList("P7988000121", "P7988000122")));
    }

    @Test
    public void isAllPackageUnloadedFailureTest() {
        PackageDistributionDTO.RouteDTO routeDTO =
            PackageDistributionDTO.RouteDTO.builder()
                .deliveries(Arrays.asList(PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder()
                    .barcode("P7988000121").state(State.UNLOADED).build(), PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder()
                    .barcode("P7988000122").state(State.UNLOADED).build())).deliveryPoint(3).build();
        assertFalse(FleetManagementUtil
            .isAllPackageUnloaded
            .test(routeDTO, Arrays.asList("P7988000121", "P7988000124")));
    }

    @Test
    public void isSackOrPackageInSackSuccessByPassingSackCodeTest() {
        PackageDistributionDTO.RouteDTO.DeliveryItemsDTO deliveryItemDTO = PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder()
            .barcode("C725799").state(State.UNLOADED).build();
        BarcodeDetailsEntity barcodeDetailsEntity = new BarcodeDetailsEntity();
        barcodeDetailsEntity.setBarcodePackage("P7988000121");
        List<BarcodeDetailsEntity> barcodeDetailsEntities =
            Arrays.asList(barcodeDetailsEntity);
        assertTrue(FleetManagementUtil
            .isSackOrPackageInSack
            .test(deliveryItemDTO, barcodeDetailsEntities));
    }

    @Test
    public void isSackOrPackageInSackSuccessByPassingPackageCodeinSackTest() {
        PackageDistributionDTO.RouteDTO.DeliveryItemsDTO deliveryItemDTO = PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder()
            .barcode("P7988000121").state(State.UNLOADED).build();
        BarcodeDetailsEntity barcodeDetailsEntity = new BarcodeDetailsEntity();
        barcodeDetailsEntity.setBarcodePackage("P7988000121");
        barcodeDetailsEntity.setBarcodeSack("C725799");
        List<BarcodeDetailsEntity> barcodeDetailsEntities = Arrays.asList(barcodeDetailsEntity);
        assertTrue(FleetManagementUtil
            .isSackOrPackageInSack
            .test(deliveryItemDTO, barcodeDetailsEntities));
    }

    @Test
    public void isSackOrPackageInSackFailureTest() {
        PackageDistributionDTO.RouteDTO.DeliveryItemsDTO deliveryItemDTO = PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder()
            .barcode("P7988000121").state(State.UNLOADED).build();
        BarcodeDetailsEntity barcodeDetailsEntity = new BarcodeDetailsEntity();
        barcodeDetailsEntity.setBarcodePackage("P7988000124");
        barcodeDetailsEntity.setBarcodeSack("C725799");
        List<BarcodeDetailsEntity> barcodeDetailsEntities = Arrays.asList(barcodeDetailsEntity);
        assertFalse(FleetManagementUtil
            .isSackOrPackageInSack
            .test(deliveryItemDTO, barcodeDetailsEntities));
    }

    @Test
    public void mergeTest() {
        List<String> oldList = Arrays.asList("C725799");
        List<String> newList = Arrays.asList("P7988000121");
        assertEquals(Arrays.asList("C725799", "P7988000121"), FleetManagementUtil.merge.apply(oldList, newList));
    }
}