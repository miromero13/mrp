package eccomerce.backend_eccomerce.engineering.repository;

import eccomerce.backend_eccomerce.engineering.entity.ProductVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductVersionRepository extends JpaRepository<ProductVersionEntity, UUID> {
}
