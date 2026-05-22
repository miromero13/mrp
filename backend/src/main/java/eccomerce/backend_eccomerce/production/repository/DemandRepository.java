package eccomerce.backend_eccomerce.production.repository;

import eccomerce.backend_eccomerce.production.entity.DemandEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DemandRepository extends JpaRepository<DemandEntity, UUID> {
    @EntityGraph(attributePaths = {"enterprise", "product"})
    List<DemandEntity> findByEnterpriseIdOrderByDueDateAscCreatedAtDesc(UUID enterpriseId);

    @EntityGraph(attributePaths = {"enterprise", "product"})
    Optional<DemandEntity> findByIdAndEnterpriseId(UUID id, UUID enterpriseId);

    @EntityGraph(attributePaths = {"enterprise", "product"})
    List<DemandEntity> findByEnterpriseIdAndProductIdAndDueDateBetweenAndStatusNotOrderByDueDateAsc(UUID enterpriseId, UUID productId, java.time.LocalDate startDate, java.time.LocalDate endDate, eccomerce.backend_eccomerce.production.enums.DemandStatus status);
}
