package eccomerce.backend_eccomerce.employee.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.employee.entity.EmployeeEntity;
import eccomerce.backend_eccomerce.employee.repository.EmployeeRepository;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseMessage<EmployeeEntity> create(EmployeeEntity dto) {
        try {
            EmployeeEntity employee = new EmployeeEntity();

            employee.setInternalCode(dto.getInternalCode());
            employee.setPosition(dto.getPosition());
            employee.setHourlyRate(dto.getHourlyRate());
            employee.setHireDate(dto.getHireDate());
            employee.setStatus(dto.getStatus());

            if (dto.getUser() != null) {
                UserEntity user = userRepository.findById(dto.getUser().getId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                employee.setUser(user);
            }

            employeeRepository.save(employee);
            return ResponseMessage.success(employee, "Empleado creado", 1);

        } catch (Exception e) {
            return ResponseMessage.error("Error al crear empleado", e.getMessage(), 500);
        }
    }

    public ResponseMessage<List<EmployeeEntity>> findAll() {
        try {
            List<EmployeeEntity> list = employeeRepository.findAll();
            return ResponseMessage.success(list, "Lista de empleados", list.size());
        } catch (Exception e) {
            return ResponseMessage.error("Error al listar", e.getMessage(), 500);
        }
    }

    public ResponseMessage<EmployeeEntity> findById(UUID id) {
        try {
            EmployeeEntity entity = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            return ResponseMessage.success(entity, "Empleado encontrado", 1);

        } catch (RuntimeException e) {
            return ResponseMessage.error("Error", e.getMessage(), 404);
        }
    }

    public ResponseMessage<EmployeeEntity> update(UUID id, EmployeeEntity dto) {
        try {
            EmployeeEntity entity = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            entity.setInternalCode(dto.getInternalCode());
            entity.setPosition(dto.getPosition());
            entity.setHourlyRate(dto.getHourlyRate());
            entity.setHireDate(dto.getHireDate());
            entity.setStatus(dto.getStatus());

            employeeRepository.save(entity);
            return ResponseMessage.success(entity, "Empleado actualizado", 1);

        } catch (RuntimeException e) {
            return ResponseMessage.error("Error", e.getMessage(), 404);
        }
    }

    public ResponseMessage<Void> delete(UUID id) {
        try {
            EmployeeEntity entity = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            employeeRepository.delete(entity);
            return ResponseMessage.success(null, "Empleado eliminado", null);

        } catch (RuntimeException e) {
            return ResponseMessage.error("Error", e.getMessage(), 404);
        }
    }
}
