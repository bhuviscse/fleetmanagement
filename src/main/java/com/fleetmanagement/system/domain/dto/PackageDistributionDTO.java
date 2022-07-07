package com.fleetmanagement.system.domain.dto;

import com.fleetmanagement.system.domain.type.State;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PackageDistributionDTO {
    private String vehicle;
    private List<RouteDTO> route;

    @RequiredArgsConstructor
    @Builder(toBuilder = true)
    @AllArgsConstructor
    @Getter
    @Setter
    public static class RouteDTO {
        private Integer deliveryPoint;
        private List<DeliveryItemsDTO> deliveries;

        @RequiredArgsConstructor
        @Builder(toBuilder = true)
        @AllArgsConstructor
        @Getter
        @Setter
        public static class DeliveryItemsDTO {
            private String barcode;
            private State state;
        }
    }
}

