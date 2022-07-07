package com.fleetmanagement.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fleetmanagement.system.domain.dto.PackageDistributionDTO;
import com.fleetmanagement.system.domain.dto.ResponseDTO;
import com.fleetmanagement.system.domain.type.State;
import com.fleetmanagement.system.service.FleetManagementService;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class FleetManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FleetManagementService fleetManagementService;


    @Test
    void distributepackageWithUnauthorizedUserTest() throws Exception {

        this.mockMvc.perform(post("/distributepackage").content(getRequestJson())
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "wrongUser", password = "wrongPassword", roles = "USER123")
    void distributepackageWithForbiddenUserTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(PackageDistributionDTO.builder().build());
        this.mockMvc.perform(post("/distributepackage")
            .content(requestJson).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void distributepackageSuccessScenario() throws Exception {
        when(fleetManagementService.updatePackageDistributionDetails(any())).thenReturn(ResponseDTO.builder().packageDistribution(getPackageDistributionDTO()).build());
        this.mockMvc.perform(post("/distributepackage")
            .content(getRequestJson()).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(
                content().string(
                    "{\"packageDistribution\":{\"vehicle\":\"B1234\",\"route\":[{\"deliveryPoint\":1,\"deliveries\":[{\"barcode\":\"P12345\",\"state\":\"UNLOADED\"}]}]}}"));
    }

    private ResponseDTO getResponse() {
        return ResponseDTO.builder()
            .packageDistribution(
                PackageDistributionDTO.builder().build()).build();
    }

    private static PackageDistributionDTO getPackageDistributionDTO() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PackageDistributionDTO packageDistributionDTO =
            PackageDistributionDTO.builder().vehicle("B1234")
                .route(Arrays.asList(PackageDistributionDTO.RouteDTO.builder().deliveryPoint(1).deliveries(Arrays.asList(
                    PackageDistributionDTO.RouteDTO.DeliveryItemsDTO.builder().barcode("P12345").state(State.UNLOADED).build())).build()))
                .build();

        objectMapper.writeValueAsString(packageDistributionDTO);

        return packageDistributionDTO;
    }

    public static String getRequestJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(getPackageDistributionDTO());
    }
}
