package eccomerce.backend_eccomerce.backup.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;

import eccomerce.backend_eccomerce.backup.entity.BackupEntity;

@Repository
//@Transactional
public interface BackupRepository extends JpaRepository<BackupEntity, Long> {

    Optional<BackupEntity> findByIdAndEnterpriseId(Long backupId, UUID empresaId);

    List<BackupEntity> findByEnterpriseIdOrderByStartDateDesc(UUID empresaId);

}
