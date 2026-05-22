package eccomerce.backend_eccomerce.warehouses.repository;

import eccomerce.backend_eccomerce.warehouses.entity.BomItemEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BomItemRepository extends JpaRepository<BomItemEntity, UUID> {
    @EntityGraph(attributePaths = {"productVersion", "productVersion.product", "material"})
    List<BomItemEntity> findByProductVersionId(UUID productVersionId);

    @EntityGraph(attributePaths = {"productVersion", "productVersion.product", "material"})
    Optional<BomItemEntity> findByIdAndProductVersionProductEnterpriseId(UUID id, UUID enterpriseId);

    Optional<BomItemEntity> findByProductVersionIdAndMaterialId(UUID productVersionId, UUID materialId);
}
