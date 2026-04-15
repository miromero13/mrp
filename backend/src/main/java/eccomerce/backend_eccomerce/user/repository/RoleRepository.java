package eccomerce.backend_eccomerce.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import eccomerce.backend_eccomerce.user.entity.RoleEntity;
import java.util.UUID;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(String name);
}
