package eccomerce.backend_eccomerce.warehouses.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.warehouses.dto.BatchDto;
import eccomerce.backend_eccomerce.warehouses.dto.CreateBatchDto;
import eccomerce.backend_eccomerce.warehouses.dto.UpdateBatchDto;
import eccomerce.backend_eccomerce.warehouses.service.BatchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprise/batches")
@Tag(name = "Batches")
public class BatchController {
    @Autowired
    private BatchService batchService;

    @RequirePermission(PermissionConstants.LISTAR_LOTE)
    @GetMapping
    public ResponseMessage<List<BatchDto>> findAll(@RequestParam(required = false) UUID enterpriseId, @RequestParam(required = false) UUID materialId, @RequestParam(required = false) UUID productId) {
        return batchService.findAll(enterpriseId, materialId, productId);
    }

    @RequirePermission(PermissionConstants.LISTAR_LOTE)
    @GetMapping("/{id}")
    public ResponseMessage<BatchDto> findById(@PathVariable UUID id) {
        return batchService.findById(id);
    }

    @RequirePermission(PermissionConstants.CREAR_LOTE)
    @PostMapping
    public ResponseMessage<BatchDto> create(@Valid @RequestBody CreateBatchDto dto) {
        return batchService.create(dto);
    }

    @RequirePermission(PermissionConstants.EDITAR_LOTE)
    @PutMapping("/{id}")
    public ResponseMessage<BatchDto> update(@PathVariable UUID id, @RequestBody UpdateBatchDto dto) {
        return batchService.update(id, dto);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_LOTE)
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return batchService.delete(id);
    }
}
