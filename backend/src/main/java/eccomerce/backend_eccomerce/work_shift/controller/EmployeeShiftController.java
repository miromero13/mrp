package eccomerce.backend_eccomerce.work_shift.controller;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.work_shift.dto.AssignShiftDto;
import eccomerce.backend_eccomerce.work_shift.entity.EmployeeShiftEntity;
import eccomerce.backend_eccomerce.work_shift.service.EmployeeShiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employee-shifts")
@Tag(name = "Employee Shifts", description = "Gestión de turnos de empleados")
public class EmployeeShiftController {

    @Autowired
    private EmployeeShiftService employeeShiftService;

    @Operation(summary = "Asignar turno")
    @PostMapping
    public ResponseMessage<EmployeeShiftEntity> assignShift(@RequestBody AssignShiftDto dto) {
        return employeeShiftService.assignShift(dto);
    }

    @Operation(summary = "Listar turnos")
    @GetMapping
    public ResponseMessage<List<EmployeeShiftEntity>> getAll() {
        return employeeShiftService.getAll();
    }

    @Operation(summary = "Eliminar turno")
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return employeeShiftService.delete(id);
    }
}