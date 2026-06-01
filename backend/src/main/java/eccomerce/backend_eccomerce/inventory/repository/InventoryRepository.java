package eccomerce.backend_eccomerce.inventory.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eccomerce.backend_eccomerce.inventory.entity.InventoryMomentDetailEntity;
import eccomerce.backend_eccomerce.inventory.entity.InventoryMomentEntity;
import eccomerce.backend_eccomerce.materials.entity.MaterialEntity;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryMomentEntity, UUID> {

    List<InventoryMomentEntity> findByType(InventoryMomentEntity.InventoryMomentType type);

    @Query("SELECT im FROM InventoryMomentEntity im WHERE im.movementDate BETWEEN :start AND :end AND (:enterpriseId IS NULL OR im.enterprise.id = :enterpriseId)")
    List<InventoryMomentEntity> findByMovementDateBetweenAndEnterprise(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("enterpriseId") UUID enterpriseId);

    @Query("SELECT imd FROM InventoryMomentDetailEntity imd " +
           "WHERE imd.material = :material " +
           "AND imd.inventoryMoment.movementDate <= :date " +
           "ORDER BY imd.inventoryMoment.movementDate DESC")
    List<InventoryMomentDetailEntity> findInventoryHistoryByMaterial(
            @Param("material") MaterialEntity material,
            @Param("date") LocalDateTime date);
}