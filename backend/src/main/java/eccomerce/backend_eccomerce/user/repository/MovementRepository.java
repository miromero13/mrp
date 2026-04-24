package eccomerce.backend_eccomerce.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eccomerce.backend_eccomerce.user.entity.MovementEntity;

@Repository
public interface MovementRepository extends JpaRepository<MovementEntity, Long> {
}
