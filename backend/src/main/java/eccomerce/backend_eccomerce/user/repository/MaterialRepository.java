package eccomerce.backend_eccomerce.user.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eccomerce.backend_eccomerce.user.entity.MaterialEntity;

@Repository
public interface MaterialRepository extends JpaRepository<MaterialEntity, UUID> {
    //Optional<MaterialEntity> findByNombre(String nombre);
}