package eccomerce.backend_eccomerce.work_shift.controller;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.work_shift.entity.WorkShiftEntity;
import eccomerce.backend_eccomerce.work_shift.service.WorkShiftService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/work-shifts")
@Tag(name = "Work Shifts")
public class WorkShiftController {
    @Autowired
    private WorkShiftService service;

    @PostMapping
    public ResponseMessage<WorkShiftEntity> create(@RequestBody WorkShiftEntity dto) {
        return service.create(dto);
    }

    @GetMapping
    public ResponseMessage<List<WorkShiftEntity>> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseMessage<WorkShiftEntity> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseMessage<WorkShiftEntity> update(@PathVariable UUID id, @RequestBody WorkShiftEntity dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return service.delete(id);
    }
}
