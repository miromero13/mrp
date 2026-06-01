package eccomerce.backend_eccomerce.materials.repository;

import eccomerce.backend_eccomerce.materials.entity.BatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<BatchEntity, UUID> {
    boolean existsByBatchNumber(String batchNumber);
}
