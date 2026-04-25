package eccomerce.backend_eccomerce.warehouses.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.warehouses.dto.CreateMaterialDto;
import eccomerce.backend_eccomerce.warehouses.dto.CreateMaterialMovementDto;
import eccomerce.backend_eccomerce.warehouses.dto.UpdateMaterialDto;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialEntity;
import eccomerce.backend_eccomerce.warehouses.entity.MaterialMovementEntity;
import eccomerce.backend_eccomerce.warehouses.service.MaterialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprise/materials")
@Tag(name = "Materials")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    @RequirePermission(PermissionConstants.CREAR_MATERIAL)
    @PostMapping
    public ResponseMessage<MaterialEntity> create(@Valid @RequestBody CreateMaterialDto dto) {
        return materialService.create(dto);
    }

    @RequirePermission(PermissionConstants.LISTAR_MATERIAL)
    @GetMapping
    public ResponseMessage<List<MaterialEntity>> findAll() {
        return materialService.findAll();
    }

    @RequirePermission(PermissionConstants.LISTAR_MATERIAL)
    @GetMapping("/{id}")
    public ResponseMessage<MaterialEntity> findById(@PathVariable UUID id) {
        return materialService.findById(id);
    }

    @RequirePermission(PermissionConstants.LISTAR_MOVIMIENTO_MATERIAL)
    @GetMapping("/movements")
    public ResponseMessage<List<MaterialMovementEntity>> findMovements() {
        return materialService.findMovements();
    }

    @RequirePermission(PermissionConstants.LISTAR_MOVIMIENTO_MATERIAL)
    @GetMapping("/{id}/movements")
    public ResponseMessage<List<MaterialMovementEntity>> findMovementsByMaterial(@PathVariable UUID id) {
        return materialService.findMovementsByMaterial(id);
    }

    @RequirePermission(PermissionConstants.CREAR_MOVIMIENTO_MATERIAL)
    @PostMapping("/{id}/movements")
    public ResponseMessage<MaterialMovementEntity> registerMovement(@PathVariable UUID id, @Valid @RequestBody CreateMaterialMovementDto dto) {
        return materialService.registerMovement(id, dto);
    }

    @RequirePermission(PermissionConstants.EDITAR_MATERIAL)
    @PutMapping("/{id}")
    public ResponseMessage<MaterialEntity> update(@PathVariable UUID id, @Valid @RequestBody UpdateMaterialDto dto) {
        return materialService.update(id, dto);
    }

    @RequirePermission(PermissionConstants.ELIMINAR_MATERIAL)
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return materialService.delete(id);
    }
}
