package eccomerce.backend_eccomerce.engineering.repository;

import eccomerce.backend_eccomerce.engineering.entity.ProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    @EntityGraph(attributePaths = {"enterprise", "materials", "versions"})
    List<ProductEntity> findByEnterpriseId(UUID enterpriseId);

    @EntityGraph(attributePaths = {"enterprise", "materials", "versions"})
    Optional<ProductEntity> findByIdAndEnterpriseId(UUID id, UUID enterpriseId);

    boolean existsByNameAndEnterpriseId(String name, UUID enterpriseId);
}
