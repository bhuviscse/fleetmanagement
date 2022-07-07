package com.fleetmanagement.system.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FleetManagementRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    FleetManagementRepository fleetManagementRepository;

    @Test
    public void findBarCodeDetailsTest() {
        assertEquals(4, fleetManagementRepository.findBarCodeDetails().size());
    }

    @Test
    public void getSackBarcodeDetailsTest() {
        assertEquals(2, fleetManagementRepository.getSackBarcodeDetails().size());
    }

    @Test
    public void getPackageBarcodeDetailsTest() {
        assertEquals(15, fleetManagementRepository.getPackageBarcodeDetails().size());
    }
}
