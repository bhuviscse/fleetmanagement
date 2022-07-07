package com.fleetmanagement.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
public class DeliveryItemsDTO {
    private String barcode;
}

