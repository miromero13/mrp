package eccomerce.backend_eccomerce.work_shift.repository;

import eccomerce.backend_eccomerce.work_shift.entity.EmployeeShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeShiftRepository extends JpaRepository<EmployeeShiftEntity, UUID> {
    boolean existsByUserIdAndDayOfWeek(UUID userId, String dayOfWeek);

    List<EmployeeShiftEntity> findByUserEnterpriseId(UUID enterpriseId);

    List<EmployeeShiftEntity> findByUserId(UUID userId);
}
