package eccomerce.backend_eccomerce.work_shift.service;

import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.work_shift.entity.WorkShiftEntity;
import eccomerce.backend_eccomerce.work_shift.repository.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import eccomerce.backend_eccomerce.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class WorkShiftService {
    @Autowired
    private WorkShiftRepository workShiftRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseMessage<WorkShiftEntity> create(WorkShiftEntity dto) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("Error al crear turno", "Superadmin no tiene acceso a turnos", 403);
            }

            WorkShiftEntity entity = new WorkShiftEntity();

            entity.setName(dto.getName());
            entity.setStartDate(dto.getStartDate());
            entity.setEndDate(dto.getEndDate());

            entity.setEnterprise(resolveEnterprise(dto.getEnterprise()));

            workShiftRepository.save(entity);
            return ResponseMessage.success(entity, "Turno creado", 1);

        } catch (Exception e) {
            return ResponseMessage.error("Error al crear turno", e.getMessage(), 500);
        }
    }

    public ResponseMessage<List<WorkShiftEntity>> findAll() {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("Error", "Superadmin no tiene acceso a turnos", 403);
            }

            UUID currentEnterpriseId = getCurrentEnterpriseId();
            List<WorkShiftEntity> list = isCurrentUserSuperadmin()
                    ? workShiftRepository.findAll()
                    : currentEnterpriseId == null
                    ? List.of()
                    : workShiftRepository.findAll().stream()
                    .filter(shift -> shift.getEnterprise() != null && shift.getEnterprise().getId().equals(currentEnterpriseId))
                    .toList();
            return ResponseMessage.success(list, "Lista de turnos", list.size());
        } catch (Exception e) {
            return ResponseMessage.error("Error", e.getMessage(), 500);
        }
    }

    public ResponseMessage<WorkShiftEntity> findById(UUID id) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("Error", "Superadmin no tiene acceso a turnos", 403);
            }

            WorkShiftEntity entity = workShiftRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            UUID currentEnterpriseId = getCurrentEnterpriseId();
            if (!isCurrentUserSuperadmin() && (currentEnterpriseId == null || entity.getEnterprise() == null || !entity.getEnterprise().getId().equals(currentEnterpriseId))) {
                return ResponseMessage.error("Error", "No puedes ver turnos fuera de tu empresa", 403);
            }

            return ResponseMessage.success(entity, "Turno encontrado", 1);

        } catch (RuntimeException e) {
            return ResponseMessage.error("Error", e.getMessage(), 404);
        }
    }

    public ResponseMessage<WorkShiftEntity> update(UUID id, WorkShiftEntity dto) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("Error", "Superadmin no tiene acceso a turnos", 403);
            }

            WorkShiftEntity entity = workShiftRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            UUID currentEnterpriseId = getCurrentEnterpriseId();
            if (!isCurrentUserSuperadmin() && (currentEnterpriseId == null || entity.getEnterprise() == null || !entity.getEnterprise().getId().equals(currentEnterpriseId))) {
                return ResponseMessage.error("Error", "No puedes modificar turnos fuera de tu empresa", 403);
            }

            entity.setName(dto.getName());
            entity.setStartDate(dto.getStartDate());
            entity.setEndDate(dto.getEndDate());

            workShiftRepository.save(entity);
            return ResponseMessage.success(entity, "Turno actualizado", 1);

        } catch (RuntimeException e) {
            return ResponseMessage.error("Error", e.getMessage(), 404);
        }
    }

    public ResponseMessage<Void> delete(UUID id) {
        try {
            if (isCurrentUserSuperadmin()) {
                return ResponseMessage.error("Error", "Superadmin no tiene acceso a turnos", 403);
            }

            WorkShiftEntity entity = workShiftRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            UUID currentEnterpriseId = getCurrentEnterpriseId();
            if (!isCurrentUserSuperadmin() && (currentEnterpriseId == null || entity.getEnterprise() == null || !entity.getEnterprise().getId().equals(currentEnterpriseId))) {
                return ResponseMessage.error("Error", "No puedes eliminar turnos fuera de tu empresa", 403);
            }

            workShiftRepository.delete(entity);
            return ResponseMessage.success(null, "Turno eliminado", null);

        } catch (RuntimeException e) {
            return ResponseMessage.error("Error", e.getMessage(), 404);
        }
    }

    private EnterpriseEntity resolveEnterprise(EnterpriseEntity dtoEnterprise) {
        if (dtoEnterprise != null && dtoEnterprise.getId() != null) {
            if (!isCurrentUserSuperadmin()) {
                UUID currentEnterpriseId = getCurrentEnterpriseId();
                if (currentEnterpriseId == null || !currentEnterpriseId.equals(dtoEnterprise.getId())) {
                    throw new RuntimeException("No puedes crear turnos fuera de tu empresa");
                }
            }

            return enterpriseRepository.findById(dtoEnterprise.getId())
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
        }

        if (isCurrentUserSuperadmin()) {
            return null;
        }

        UUID currentEnterpriseId = getCurrentEnterpriseId();
        if (currentEnterpriseId == null) {
            throw new RuntimeException("No tienes una empresa asignada");
        }

        return enterpriseRepository.findById(currentEnterpriseId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
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
}
