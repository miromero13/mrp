package eccomerce.backend_eccomerce.work_shift.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.employee.entity.EmployeeEntity;
import eccomerce.backend_eccomerce.employee.repository.EmployeeRepository;
import eccomerce.backend_eccomerce.work_shift.dto.AssignShiftDto;
import eccomerce.backend_eccomerce.work_shift.entity.EmployeeShiftEntity;
import eccomerce.backend_eccomerce.work_shift.entity.WorkShiftEntity;
import eccomerce.backend_eccomerce.work_shift.repository.EmployeeShiftRepository;
import eccomerce.backend_eccomerce.work_shift.repository.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeShiftService {
   /* @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkShiftRepository workShiftRepository;

    @Autowired
    private EmployeeShiftRepository employeeShiftRepository;

    public ResponseMessage<EmployeeShiftEntity> assignShift(AssignShiftDto dto) {
        try {

            EmployeeEntity employee = employeeRepository.findById(dto.employeeId)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            WorkShiftEntity workShift = workShiftRepository.findById(dto.workShiftId)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            // 🔥 Validación importante (regla de negocio real)
            boolean exists = employeeShiftRepository
                    .existsByEmployeeIdAndDayOfWeek(dto.employeeId, dto.dayOfWeek);

            if (exists) {
                return ResponseMessage.error(
                        "Conflicto de turno",
                        "El empleado ya tiene un turno asignado para ese día",
                        400
                );
            }

            EmployeeShiftEntity entity = new EmployeeShiftEntity();
            entity.setEmployee(employee);
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
            List<EmployeeShiftEntity> list = employeeShiftRepository.findAll();
            return ResponseMessage.success(list, "Lista de turnos", list.size());
        } catch (Exception ex) {
            return ResponseMessage.error("Error al listar turnos", ex.getMessage(), 500);
        }
    }

    public ResponseMessage<Void> delete(UUID id) {
        try {
            EmployeeShiftEntity entity = employeeShiftRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

            employeeShiftRepository.delete(entity);
            return ResponseMessage.success(null, "Turno eliminado", null);

        } catch (RuntimeException ex) {
            return ResponseMessage.error("Error al eliminar turno", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("Error al eliminar turno", ex.getMessage(), 500);
        }
    }*/
}
