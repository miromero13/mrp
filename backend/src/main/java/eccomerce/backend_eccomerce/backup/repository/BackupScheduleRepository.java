package eccomerce.backend_eccomerce.backup.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eccomerce.backend_eccomerce.backup.entity.BackupScheduleEntity;

@Repository
public interface BackupScheduleRepository extends JpaRepository<BackupScheduleEntity, Long> {
    Optional<BackupScheduleEntity> findByEnterpriseId(UUID enterpriseId);
}
