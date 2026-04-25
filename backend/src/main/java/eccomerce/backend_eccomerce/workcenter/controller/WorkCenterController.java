package eccomerce.backend_eccomerce.workcenter.controller;

import eccomerce.backend_eccomerce.workcenter.dto.CreateWorkCenterDto;
import eccomerce.backend_eccomerce.workcenter.dto.UpdateWorkCenterDto;
import eccomerce.backend_eccomerce.workcenter.dto.WorkCenterResponseDto;
import eccomerce.backend_eccomerce.workcenter.service.WorkCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/work-centers")
@RequiredArgsConstructor
@Tag(name = "Work Centers", description = "API para gestionar centros de trabajo")
public class WorkCenterController {

    private final WorkCenterService workCenterService;

    @PostMapping
    @Operation(summary = "Crear nuevo centro de trabajo")
    public ResponseEntity<WorkCenterResponseDto> createWorkCenter(@Valid @RequestBody CreateWorkCenterDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workCenterService.createWorkCenter(dto));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los centros de trabajo")
    public ResponseEntity<List<WorkCenterResponseDto>> getAllWorkCenters() {
        return ResponseEntity.ok(workCenterService.getAllWorkCenters());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener centro de trabajo por ID")
    public ResponseEntity<WorkCenterResponseDto> getWorkCenter(@PathVariable UUID id) {
        return ResponseEntity.ok(workCenterService.getWorkCenterById(id));
    }

    @GetMapping("/plant/{plant}")
    @Operation(summary = "Obtener centros de trabajo por planta")
    public ResponseEntity<List<WorkCenterResponseDto>> getByPlant(@PathVariable String plant) {
        return ResponseEntity.ok(workCenterService.getWorkCentersByPlant(plant));
    }

    @GetMapping("/line/{productionLine}")
    @Operation(summary = "Obtener centros de trabajo por línea productiva")
    public ResponseEntity<List<WorkCenterResponseDto>> getByProductionLine(@PathVariable String productionLine) {
        return ResponseEntity.ok(workCenterService.getWorkCentersByProductionLine(productionLine));
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener centros de trabajo activos")
    public ResponseEntity<List<WorkCenterResponseDto>> getActiveWorkCenters() {
        return ResponseEntity.ok(workCenterService.getActiveWorkCenters());
    }

    @GetMapping("/critical-resources")
    @Operation(summary = "Obtener recursos críticos")
    public ResponseEntity<List<WorkCenterResponseDto>> getCriticalResources() {
        return ResponseEntity.ok(workCenterService.getCriticalResources());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar centro de trabajo")
    public ResponseEntity<WorkCenterResponseDto> updateWorkCenter(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkCenterDto dto) {
        return ResponseEntity.ok(workCenterService.updateWorkCenter(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar centro de trabajo")
    public ResponseEntity<Void> deleteWorkCenter(@PathVariable UUID id) {
        workCenterService.deleteWorkCenter(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inactive")
    @Operation(summary = "Marcar centro como inactivo")
    public ResponseEntity<Void> markAsInactive(@PathVariable UUID id) {
        workCenterService.markAsInactive(id);
        return ResponseEntity.noContent().build();
    }
}
