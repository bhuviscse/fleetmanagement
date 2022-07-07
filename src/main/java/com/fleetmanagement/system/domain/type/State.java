/*
 * Copyright (c) 2018 The Emirates Group. All Rights Reserved.
 * The information specified here is confidential and remains property of the Emirates Group.
 * groupId     - com.emirates.ocsl
 * artifactId  - order-reservation-service
 * name        - order-reservation-service
 * description - Order Reservation Service
 * 2019
 */
package com.fleetmanagement.system.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum State {
    CREATED("1"),
    LOADEDINTOSACK("2"),
    LOADED("3"),
    UNLOADED("4");
    private String state;
}
