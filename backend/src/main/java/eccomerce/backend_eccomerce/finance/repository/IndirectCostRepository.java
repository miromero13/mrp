package eccomerce.backend_eccomerce.finance.repository;

import eccomerce.backend_eccomerce.finance.entity.IndirectCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndirectCostRepository extends JpaRepository<IndirectCostEntity, UUID> {
    List<IndirectCostEntity> findByEnterpriseIdOrderByCreatedAtDesc(UUID enterpriseId);

    Optional<IndirectCostEntity> findByIdAndEnterpriseId(UUID id, UUID enterpriseId);
}
