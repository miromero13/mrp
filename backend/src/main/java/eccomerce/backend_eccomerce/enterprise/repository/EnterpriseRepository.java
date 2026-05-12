package eccomerce.backend_eccomerce.enterprise.repository;

import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EnterpriseRepository extends JpaRepository<EnterpriseEntity, UUID> {
    Optional<EnterpriseEntity> findByNit(String nit);
}
