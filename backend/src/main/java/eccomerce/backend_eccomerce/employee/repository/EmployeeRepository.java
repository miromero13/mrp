package eccomerce.backend_eccomerce.employee.repository;

import eccomerce.backend_eccomerce.employee.entity.EmployeeEntity;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.work_shift.entity.EmployeeShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID>  {

    // Buscar por código interno
    Optional<EmployeeEntity> findByInternalCode(String internalCode);

    // Validar si ya existe un código interno
    boolean existsByInternalCode(String internalCode);

    // Buscar empleado por usuario (por ID)
    Optional<EmployeeEntity> findByUserId(UUID userId);

    // 🔥 IMPORTANTE: para DataInitializer (objeto completo)
    Optional<EmployeeEntity> findByUser(UserEntity user);

    // Filtrar por estado (activo/inactivo)
    List<EmployeeEntity> findByStatus(Boolean status);

    // 🔥 EXTRA: validar existencia por usuario
    boolean existsByUserId(UUID userId);
}
