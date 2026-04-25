package eccomerce.backend_eccomerce.warehouses.repository;

import eccomerce.backend_eccomerce.warehouses.entity.MaterialEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialRepository extends JpaRepository<MaterialEntity, UUID> {
    @EntityGraph(attributePaths = {"enterprise"})
    List<MaterialEntity> findByEnterpriseId(UUID enterpriseId);

    @EntityGraph(attributePaths = {"enterprise"})
    Optional<MaterialEntity> findByIdAndEnterpriseId(UUID id, UUID enterpriseId);

    boolean existsByCodeAndEnterpriseId(String code, UUID enterpriseId);
}
