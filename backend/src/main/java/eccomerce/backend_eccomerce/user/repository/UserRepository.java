package eccomerce.backend_eccomerce.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import eccomerce.backend_eccomerce.user.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {            
    Optional<UserEntity> findByPhone(String phone);

    @EntityGraph(attributePaths = {"role", "role.permissions"})
    Optional<UserEntity> findByEmail(String email);
}

