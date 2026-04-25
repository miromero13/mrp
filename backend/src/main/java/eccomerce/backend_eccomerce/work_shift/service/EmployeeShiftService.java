package eccomerce.backend_eccomerce.work_shift.service;

import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.user.repository.UserRepository;
import eccomerce.backend_eccomerce.work_shift.dto.AssignShiftDto;
import eccomerce.backend_eccomerce.work_shift.entity.EmployeeShiftEntity;
import eccomerce.backend_eccomerce.work_shift.entity.WorkShiftEntity;
import eccomerce.backend_eccomerce.work_shift.repository.EmployeeShiftRepository;
import eccomerce.backend_eccomerce.work_shift.repository.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeShiftService {

    @Autowired
    private WorkShiftRepository workShiftRepository;

    @Autowired
    private EmployeeShiftRepository employeeShiftRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseMessage<EmployeeShiftEntity> assignShift(AssignShiftDto dto) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("Error al asignar turno", "Superadmin no tiene acceso a turnos", 403);
            }

            UserEntity user = userRepository.findById(dto.employeeId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (user.role == null || !RoleConstants.EMPLOYEE.equalsIgnoreCase(user.role.name)) {
                return ResponseMessage.error("Error al asignar turno", "El usuario no tiene rol employee", 400);
            }

            WorkShiftEntity workShift = workShiftRepository.findById(dto.workShiftId)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            if (!canAccessEnterprise(user.enterprise == null ? null : user.enterprise.getId(), workShift.getEnterprise() == null ? null : workShift.getEnterprise().getId())) {
                return ResponseMessage.error("Error al asignar turno", "No puedes asignar turnos fuera de tu empresa", 403);
            }

            boolean exists = employeeShiftRepository.existsByUserIdAndDayOfWeek(dto.employeeId, dto.dayOfWeek);
            if (exists) {
                return ResponseMessage.error(
                        "Conflicto de turno",
                        "El empleado ya tiene un turno asignado para ese día",
                        400
                );
            }

            EmployeeShiftEntity entity = new EmployeeShiftEntity();
            entity.setUser(user);
            entity.setWorkShift(workShift);
            entity.setDayOfWeek(dto.dayOfWeek);

            employeeShiftRepository.save(entity);

            return ResponseMessage.success(entity, "Turno asignado correctamente", 1);

        } catch (RuntimeException ex) {
            return ResponseMessage.error("Error al asignar turno", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error(
                    "Error al asignar turno",
                    "Error interno: " + ex.getMessage(),
                    500
            );
        }
    }

    public ResponseMessage<List<EmployeeShiftEntity>> getAll() {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("Error al listar turnos", "Superadmin no tiene acceso a turnos", 403);
            }

            UUID currentEnterpriseId = getCurrentEnterpriseId();
            List<EmployeeShiftEntity> list = isCurrentUserSuperadmin()
                    ? employeeShiftRepository.findAll()
                    : currentEnterpriseId == null ? List.of() : employeeShiftRepository.findByUserEnterpriseId(currentEnterpriseId);
            return ResponseMessage.success(list, "Lista de turnos", list.size());
        } catch (Exception ex) {
            return ResponseMessage.error("Error al listar turnos", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<Void> delete(UUID id) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("Error al eliminar turno", "Superadmin no tiene acceso a turnos", 403);
            }

            EmployeeShiftEntity entity = employeeShiftRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

            UUID currentEnterpriseId = getCurrentEnterpriseId();
            if (!isCurrentUserSuperadmin() && !canAccessEnterprise(entity.getUser() == null || entity.getUser().enterprise == null ? null : entity.getUser().enterprise.getId(), currentEnterpriseId)) {
                return ResponseMessage.error("Error al eliminar turno", "No puedes eliminar turnos fuera de tu empresa", 403);
            }

            employeeShiftRepository.delete(entity);
            return ResponseMessage.success(null, "Turno eliminado", null);

        } catch (RuntimeException ex) {
            return ResponseMessage.error("Error al eliminar turno", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("Error al eliminar turno", ex.getMessage(), 500);
        }
    }

    private boolean isCurrentUserSuperadmin() {
        String email = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null) {
            return false;
        }

        return userRepository.findByEmail(email)
                .map(user -> user.role != null && RoleConstants.SUPERADMIN.equalsIgnoreCase(user.role.name))
                .orElse(false);
    }

    private UUID getCurrentEnterpriseId() {
        String email = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null) {
            return null;
        }

        return userRepository.findByEmail(email)
                .map(user -> user.enterprise == null ? null : user.enterprise.getId())
                .orElse(null);
    }

    private boolean canAccessEnterprise(UUID subjectEnterpriseId, UUID currentEnterpriseId) {
        if (isCurrentUserSuperadmin()) {
            return true;
        }

        return subjectEnterpriseId != null && subjectEnterpriseId.equals(currentEnterpriseId);
    }
}
