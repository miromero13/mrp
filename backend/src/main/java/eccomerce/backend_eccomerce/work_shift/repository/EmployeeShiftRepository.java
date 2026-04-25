package eccomerce.backend_eccomerce.work_shift.repository;

import eccomerce.backend_eccomerce.employee.entity.EmployeeEntity;
import eccomerce.backend_eccomerce.work_shift.entity.EmployeeShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeShiftRepository extends JpaRepository<EmployeeShiftEntity, UUID> {

    // Validar si ya existe asignación por día
    //boolean existsByEmployeeIdAndDayOfWeek(UUID employeeId, String dayOfWeek);

    // 🔥 ESTE ES EL QUE TE FALTA
    //List<EmployeeShiftEntity> findByEmployee(EmployeeEntity employee);
}
