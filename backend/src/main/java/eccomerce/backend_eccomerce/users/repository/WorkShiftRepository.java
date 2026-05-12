package eccomerce.backend_eccomerce.users.repository;

import eccomerce.backend_eccomerce.users.entity.WorkShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkShiftRepository extends JpaRepository<WorkShiftEntity, UUID> {
    // Obtener turnos por empresa (clave para SaaS 🔥)
    List<WorkShiftEntity> findByEnterpriseId(UUID enterpriseId);

    // Validar si existe un turno con mismo nombre en la empresa
    boolean existsByNameAndEnterpriseId(String name, UUID enterpriseId);
}
