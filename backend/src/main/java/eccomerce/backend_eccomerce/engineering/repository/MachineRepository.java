package eccomerce.backend_eccomerce.engineering.repository;

import eccomerce.backend_eccomerce.engineering.entity.MachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MachineRepository extends JpaRepository<MachineEntity, UUID> {
    List<MachineEntity> findByEnterpriseIdOrderByCreatedAtDesc(UUID enterpriseId);

    Optional<MachineEntity> findByIdAndEnterpriseId(UUID id, UUID enterpriseId);
}
