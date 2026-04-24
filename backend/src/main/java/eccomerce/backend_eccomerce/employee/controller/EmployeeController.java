package eccomerce.backend_eccomerce.employee.controller;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.employee.entity.EmployeeEntity;
import eccomerce.backend_eccomerce.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employees")
public class EmployeeController {
    @Autowired
    private EmployeeService service;

    @PostMapping
    public ResponseMessage<EmployeeEntity> create(@RequestBody EmployeeEntity dto) {
        return service.create(dto);
    }

    @GetMapping
    public ResponseMessage<List<EmployeeEntity>> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseMessage<EmployeeEntity> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseMessage<EmployeeEntity> update(@PathVariable UUID id, @RequestBody EmployeeEntity dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return service.delete(id);
    }
}
