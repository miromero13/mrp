package eccomerce.backend_eccomerce.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eccomerce.backend_eccomerce.user.entity.PermissionEntity;
import java.util.UUID;
import java.util.Optional;


@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {
    Optional<PermissionEntity> findByName(String name);
}
