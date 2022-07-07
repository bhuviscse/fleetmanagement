package com.fleetmanagement.system.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
public class RouteDTO {
    private Integer deliveryPoint;
    private List<DeliveryItemsDTO> deliveries;
}

