package eccomerce.backend_eccomerce.user.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.CreateMaterialDto;
import eccomerce.backend_eccomerce.user.dto.CreateMovementDto;
import eccomerce.backend_eccomerce.user.dto.UpdateMaterialDto;
import eccomerce.backend_eccomerce.user.entity.MaterialEntity;
import eccomerce.backend_eccomerce.user.service.MaterialService;
import eccomerce.backend_eccomerce.user.service.MovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/materials")
@Tag(name = "Materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MovementService movementService;

    //@RequirePermission(PermissionConstants.CREAR_MATERIAL)
    @Operation(summary = "Create a new material")
    @PostMapping
    public ResponseMessage<MaterialEntity> createMaterial(@RequestBody CreateMaterialDto createMaterialDto) {
        return materialService.createMaterial(createMaterialDto);
    }

    @Operation(summary = "Update an existing material")
    @PutMapping("/{id}")
    public ResponseMessage<MaterialEntity> updateMaterial(@PathVariable @NonNull UUID id, @RequestBody UpdateMaterialDto updateMaterialDto) {
        return materialService.updateMaterial(id, updateMaterialDto);
    }

    @Operation(summary = "Log a revenue for a material")
    @PostMapping("/{id}/revenue")
    public ResponseMessage<Void> logRevenue(@PathVariable @NonNull UUID id, @RequestBody CreateMovementDto createMovementlDto) {
        return movementService.logRevenue(id, createMovementlDto);        
    }

    @Operation(summary = "Log an expense for a material")
    @PostMapping("/{id}/expense")
    public ResponseMessage<Void> logExpense(@PathVariable @NonNull UUID id, @RequestBody CreateMovementDto createMovementDto) {
        return movementService.logExpense(id, createMovementDto);
    }

    @Operation(summary = "Get a material by ID")
    @GetMapping("/{id}")
    public ResponseMessage<MaterialEntity> getMaterialById(@PathVariable @NonNull UUID id) {
        return materialService.getMaterialById(id);
    }

    @Operation(summary = "Get all materials")
    @GetMapping
    public ResponseMessage<List<MaterialEntity>> listMaterial() {
        return materialService.getAllMaterials();
    }

    @Operation(summary = "Delete a material")
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> deleteMaterial(@PathVariable @NonNull UUID id) {
        return materialService.deleteMaterial(id);
    }
}
