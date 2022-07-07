package com.fleetmanagement.system;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FleetManagementApplicationTest {
    @Autowired
    FleetManagementApplication fleetManagementApplication;

    @Test
    void contextLoads() {
        assertNotNull(fleetManagementApplication);
    }

}
