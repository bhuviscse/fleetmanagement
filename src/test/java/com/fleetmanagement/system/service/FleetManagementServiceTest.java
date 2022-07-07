package com.fleetmanagement.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetmanagement.system.domain.dto.PackageDistributionDTO;
import com.fleetmanagement.system.domain.dto.ResponseDTO;
import com.fleetmanagement.system.domain.type.State;
import com.fleetmanagement.system.entity.ShipmentDetailsEntity;
import com.fleetmanagement.system.repository.FleetManagementRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class FleetManagementServiceTest {

    @Autowired
    FleetManagementService fleetManagementService;

    @Autowired
    FleetManagementRepository fleetManagementRepository;


    @Test
    public void testUpdatePackageDistributionDetailsSuccessScenario() throws IOException, JSONException {
        String output = Files.readString(Paths.get("src/test/resources/packagedistribution_success_output.json"));
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDTO response = fleetManagementService
            .updatePackageDistributionDetails(readJson("src/test/resources/packagedistribution_sucess_input.json"));
        assertNotNull(response);
        assertNotNull(response.getPackageDistribution());
        JSONAssert.assertEquals(objectMapper.writeValueAsString(response), output, true);
    }

    @Test
    public void testOnlySackDitributionInBranch() throws IOException {
        ResponseDTO response =
            fleetManagementService.updatePackageDistributionDetails(readJson("src/test/resources/branch_with_only_sack.json"));
        assertNotNull(response);
        assertNotNull(response.getPackageDistribution());
        assertEquals("C725799", response.getPackageDistribution().getRoute().get(0).getDeliveries().get(0).getBarcode());
        assertEquals(State.LOADED, response.getPackageDistribution().getRoute().get(0).getDeliveries().get(0).getState());
    }

    @Test
    public void testOnlySackDitributionInDistributionCenter() throws IOException {
        ResponseDTO response =
            fleetManagementService.updatePackageDistributionDetails(readJson("src/test/resources/distribution_center_with_only_sack.json"));
        assertNotNull(response);
        assertNotNull(response.getPackageDistribution());
        assertEquals("C725799", response.getPackageDistribution().getRoute().get(0).getDeliveries().get(0).getBarcode());
        assertEquals(State.UNLOADED, response.getPackageDistribution().getRoute().get(0).getDeliveries().get(0).getState());
    }

    @Test
    public void testOnlyPackageDitributionInTransferCenter() throws IOException {
        ResponseDTO response =
            fleetManagementService.updatePackageDistributionDetails(readJson("src/test/resources/transfer_center_with_only_package.json"));
        assertNotNull(response);
        assertNotNull(response.getPackageDistribution());
        assertEquals("P7988000121", response.getPackageDistribution().getRoute().get(0).getDeliveries().get(0).getBarcode());
        assertEquals(State.LOADED, response.getPackageDistribution().getRoute().get(0).getDeliveries().get(0).getState());
    }

    @Test
    public void testUpdateSackWhenAllPackagesUnloaded() {
        final Map<String, List<String>> sackPackageMappingMap = new HashMap<>();
        List<ShipmentDetailsEntity> shipmentDetails = new ArrayList<>();
        sackPackageMappingMap.put("C725799", Arrays.asList("P8988000122", "P8988000126"));
        sackPackageMappingMap.put("C725800", Arrays.asList("P9988000128", "P9988000129"));
        PackageDistributionDTO.RouteDTO inputPackageDistribution = PackageDistributionDTO.RouteDTO.builder().deliveries(Arrays.asList(
            PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder().barcode("P9988000128").state(State.UNLOADED).build(),
            PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder().barcode("P9988000129").state(State.UNLOADED).build())).build();
        fleetManagementService.updateSackStatusBasedOnPackageStatus(sackPackageMappingMap
            , inputPackageDistribution, shipmentDetails);
        assertFalse(shipmentDetails.isEmpty());
        assertEquals(shipmentDetails.get(0).getStatus(), State.UNLOADED.name());
    }

    public PackageDistributionDTO readJson(final String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PackageDistributionDTO packageDistribution = objectMapper.readValue(new File(path), PackageDistributionDTO.class);
        return packageDistribution;
    }

}
