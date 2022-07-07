package com.fleetmanagement.system.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Table(name = "SHIPMENT_DETAILS")
@Builder(toBuilder = true)
public class ShipmentDetailsEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "barcode")
    private String barcode;
    @Column(name = "status")
    private String status;
}
