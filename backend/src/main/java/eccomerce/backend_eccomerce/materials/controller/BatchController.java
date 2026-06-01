package eccomerce.backend_eccomerce.materials.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.materials.dto.BatchDto;
import eccomerce.backend_eccomerce.materials.dto.CreateBatchRequest;
import eccomerce.backend_eccomerce.materials.service.BatchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/batches")
@Tag(name = "Batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping
    @RequirePermission(PermissionConstants.LISTAR_LOTE)
    public ResponseEntity<List<BatchDto>> getAllBatches() {
        return ResponseEntity.ok(batchService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionConstants.LISTAR_LOTE)
    public ResponseEntity<BatchDto> getBatchById(@PathVariable UUID id) {
        return ResponseEntity.ok(batchService.findById(id));
    }

    @PostMapping
    @RequirePermission(PermissionConstants.CREAR_LOTE)
    public ResponseEntity<BatchDto> createBatch(@RequestBody CreateBatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(batchService.create(request));
    }

    @PutMapping("/{id}")
    @RequirePermission(PermissionConstants.EDITAR_LOTE)
    public ResponseEntity<BatchDto> updateBatch(@PathVariable UUID id, @RequestBody CreateBatchRequest request) {
        return ResponseEntity.ok(batchService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(PermissionConstants.ELIMINAR_LOTE)
    public ResponseEntity<Void> deleteBatch(@PathVariable UUID id) {
        batchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
