package eccomerce.backend_eccomerce.warehouses.repository;

import eccomerce.backend_eccomerce.warehouses.entity.MaterialMovementEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MaterialMovementRepository extends JpaRepository<MaterialMovementEntity, UUID> {
    @Query("select distinct m from MaterialMovementEntity m join fetch m.material material join fetch material.enterprise enterprise left join fetch m.details details where enterprise.id = :enterpriseId order by m.createdAt desc")
    List<MaterialMovementEntity> findByEnterpriseIdOrderByCreatedAtDesc(@Param("enterpriseId") UUID enterpriseId);

    @EntityGraph(attributePaths = {"material", "material.enterprise", "details"})
    List<MaterialMovementEntity> findByMaterialIdOrderByCreatedAtDesc(UUID materialId);
}
