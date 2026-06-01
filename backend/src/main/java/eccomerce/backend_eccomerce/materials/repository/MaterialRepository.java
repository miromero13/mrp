package eccomerce.backend_eccomerce.materials.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eccomerce.backend_eccomerce.materials.entity.MaterialEntity;

@Repository
public interface MaterialRepository extends JpaRepository<MaterialEntity, UUID> {

    List<MaterialEntity> findByIsActiveTrue();

    @Query("SELECT m FROM MaterialEntity m WHERE m.isActive = true AND m.stockCurrent < m.stockMin AND (:enterpriseId IS NULL OR m.enterprise.id = :enterpriseId)")
    List<MaterialEntity> findMaterialsBelowMinStockAndEnterprise(@Param("enterpriseId") UUID enterpriseId);

    @Query("SELECT m FROM MaterialEntity m WHERE m.isActive = true AND m.stockCurrent > (m.stockMin * 3) AND (:enterpriseId IS NULL OR m.enterprise.id = :enterpriseId)")
    List<MaterialEntity> findMaterialsWithExcessStockAndEnterprise(@Param("enterpriseId") UUID enterpriseId);

    @Query("SELECT m FROM MaterialEntity m WHERE m.isActive = true AND (:enterpriseId IS NULL OR m.enterprise.id = :enterpriseId)")
    List<MaterialEntity> findAllActiveAndEnterprise(@Param("enterpriseId") UUID enterpriseId);

    @Query("SELECT m FROM MaterialEntity m WHERE m.isActive = true AND m.stockCurrent > 0 AND (:enterpriseId IS NULL OR m.enterprise.id = :enterpriseId)")
    List<MaterialEntity> findMaterialsWithStockAndEnterprise(@Param("enterpriseId") UUID enterpriseId);
}