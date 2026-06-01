package eccomerce.backend_eccomerce.enterprise.repository;

import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnterpriseRepository extends JpaRepository<EnterpriseEntity, UUID> {
}
