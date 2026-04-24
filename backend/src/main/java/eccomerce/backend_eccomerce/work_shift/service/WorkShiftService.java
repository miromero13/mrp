package eccomerce.backend_eccomerce.work_shift.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.work_shift.entity.WorkShiftEntity;
import eccomerce.backend_eccomerce.work_shift.repository.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WorkShiftService {
    @Autowired
    private WorkShiftRepository workShiftRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    public ResponseMessage<WorkShiftEntity> create(WorkShiftEntity dto) {
        try {
            WorkShiftEntity entity = new WorkShiftEntity();

            entity.setName(dto.getName());
            entity.setStartDate(dto.getStartDate());
            entity.setEndDate(dto.getEndDate());

            if (dto.getEnterprise() != null) {
                EnterpriseEntity enterprise = enterpriseRepository
                        .findById(dto.getEnterprise().getId())
                        .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

                entity.setEnterprise(enterprise);
            }

            workShiftRepository.save(entity);
            return ResponseMessage.success(entity, "Turno creado", 1);

        } catch (Exception e) {
            return ResponseMessage.error("Error al crear turno", e.getMessage(), 500);
        }
    }

    public ResponseMessage<List<WorkShiftEntity>> findAll() {
        try {
            List<WorkShiftEntity> list = workShiftRepository.findAll();
            return ResponseMessage.success(list, "Lista de turnos", list.size());
        } catch (Exception e) {
            return ResponseMessage.error("Error", e.getMessage(), 500);
        }
    }

    public ResponseMessage<WorkShiftEntity> findById(UUID id) {
        try {
            WorkShiftEntity entity = workShiftRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            return ResponseMessage.success(entity, "Turno encontrado", 1);

        } catch (RuntimeException e) {
            return ResponseMessage.error("Error", e.getMessage(), 404);
        }
    }

    public ResponseMessage<WorkShiftEntity> update(UUID id, WorkShiftEntity dto) {
        try {
            WorkShiftEntity entity = workShiftRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

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
            WorkShiftEntity entity = workShiftRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            workShiftRepository.delete(entity);
            return ResponseMessage.success(null, "Turno eliminado", null);

        } catch (RuntimeException e) {
            return ResponseMessage.error("Error", e.getMessage(), 404);
        }
    }
}
