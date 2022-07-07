package com.fleetmanagement.system.domain.dto;

import lombok.Data;

@Data
public class ExceptionDTO {
    private String errorCode;
    private String errorDescription;
}
