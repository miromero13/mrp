package eccomerce.backend_eccomerce.enterprise.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.enterprise.dto.CreateEnterpriseDto;
import eccomerce.backend_eccomerce.enterprise.dto.UpdateEnterpriseDto;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.service.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enterprises")
@Tag(name = "Enterprises", description = "API para la gestión de empresas")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    // CREATE
    @RequirePermission(PermissionConstants.CREAR_EMPRESA)
    @Operation(summary = "Crear una nueva empresa")
    @PostMapping
    public ResponseMessage<EnterpriseEntity> create(@RequestBody CreateEnterpriseDto dto) {
        return enterpriseService.create(dto);
    }

    // UPDATE
    @RequirePermission(PermissionConstants.EDITAR_EMPRESA)
    @Operation(summary = "Actualizar una empresa")
    @PutMapping("/{id}")
    public ResponseMessage<EnterpriseEntity> update(
            @PathVariable UUID id,
            @RequestBody UpdateEnterpriseDto dto
    ) {
        return enterpriseService.update(id, dto);
    }

    // GET BY ID
    @RequirePermission(PermissionConstants.LISTAR_EMPRESA)
    @Operation(summary = "Obtener empresa por ID")
    @GetMapping("/{id}")
    public ResponseMessage<EnterpriseEntity> findById(@PathVariable UUID id) {
        return enterpriseService.findById(id);
    }

    // DELETE
    @RequirePermission(PermissionConstants.ELIMINAR_EMPRESA)
    @Operation(summary = "Eliminar empresa")
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> delete(@PathVariable UUID id) {
        return enterpriseService.delete(id);
    }

    // GET ALL
    @RequirePermission(PermissionConstants.LISTAR_EMPRESA)
    @Operation(summary = "Listar todas las empresas")
    @GetMapping
    public ResponseMessage<List<EnterpriseEntity>> findAll() {
        return enterpriseService.findAll();
    }
}