package com.fleetmanagement.system.repository;

import com.fleetmanagement.system.entity.BarcodeDetailsEntity;
import com.fleetmanagement.system.entity.DeliveryPointEntity;
import com.fleetmanagement.system.entity.PackageBarcodeEntity;
import com.fleetmanagement.system.entity.SackBarcodeEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FleetManagementRepository {
    @PersistenceContext
    EntityManager em;
    private static final String DELIVERY_POINT = "deliveryPoint";
    private static final String ID = "id";
    private final BarcodeDetailsRepository BarcodeDetailsRepository;
    @Autowired
    public FleetManagementRepository(BarcodeDetailsRepository BarcodeDetailsRepository){
        this.BarcodeDetailsRepository=BarcodeDetailsRepository;
    }

    public List<BarcodeDetailsEntity> findBarCodeDetails(){
        return BarcodeDetailsRepository.findAll();
    }
    public List<PackageBarcodeEntity> getPackageBarcodeDetails() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PackageBarcodeEntity> query = cb.createQuery(PackageBarcodeEntity.class);
        Root<DeliveryPointEntity> aRoot = query.from(DeliveryPointEntity.class);
        Root<PackageBarcodeEntity> bRoot = query.from(PackageBarcodeEntity.class);
        query.select(bRoot).distinct(true);
        Predicate barcodeDeliveryPntPredicate = cb.equal(aRoot.get(ID), bRoot.get(DELIVERY_POINT));
        query.where(barcodeDeliveryPntPredicate);
        return em.createQuery(query).getResultList();
    }

    public List<SackBarcodeEntity> getSackBarcodeDetails() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SackBarcodeEntity> query = cb.createQuery(SackBarcodeEntity.class);
        Root<DeliveryPointEntity> aRoot = query.from(DeliveryPointEntity.class);
        Root<SackBarcodeEntity> bRoot = query.from(SackBarcodeEntity.class);
        query.select(bRoot).distinct(true);
        Predicate barcodeDeliveryPntPredicate = cb.equal(aRoot.get(ID), bRoot.get(DELIVERY_POINT));
        query.where(barcodeDeliveryPntPredicate);
        return em.createQuery(query).getResultList();
    }
}
