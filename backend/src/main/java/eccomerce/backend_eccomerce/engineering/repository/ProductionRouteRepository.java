package eccomerce.backend_eccomerce.engineering.repository;

import eccomerce.backend_eccomerce.engineering.entity.ProductionRouteEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductionRouteRepository extends JpaRepository<ProductionRouteEntity, UUID> {
    @EntityGraph(attributePaths = {"productVersion", "productVersion.product", "operations", "operations.machine"})
    List<ProductionRouteEntity> findByProductVersionProductEnterpriseIdOrderByCreatedAtDesc(UUID enterpriseId);

    @EntityGraph(attributePaths = {"productVersion", "productVersion.product", "operations", "operations.machine"})
    List<ProductionRouteEntity> findByProductVersionIdOrderByCreatedAtDesc(UUID productVersionId);

    @EntityGraph(attributePaths = {"productVersion", "productVersion.product", "operations", "operations.machine"})
    Optional<ProductionRouteEntity> findByIdAndProductVersionProductEnterpriseId(UUID id, UUID enterpriseId);

    List<ProductionRouteEntity> findByProductVersionIdAndIsActiveTrue(UUID productVersionId);
}
