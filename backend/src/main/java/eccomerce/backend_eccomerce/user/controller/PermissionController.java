package eccomerce.backend_eccomerce.user.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.CreatePermissionDto;
import eccomerce.backend_eccomerce.user.dto.UpdatePermissionDto;
import eccomerce.backend_eccomerce.user.entity.PermissionEntity;
import eccomerce.backend_eccomerce.user.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@Tag(name = "Permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @RequirePermission(PermissionConstants.CREAR_PERMISO)
    @Operation(summary = "Create a new permission")
    @PostMapping
    public ResponseMessage<PermissionEntity> createPermission(@RequestBody CreatePermissionDto createPermissionDto) {
        return permissionService.createPermission(createPermissionDto);
    }
    
    @RequirePermission(PermissionConstants.LISTAR_PERMISO)
    @Operation(summary = "Get a permission by ID")
    @GetMapping("/{id}")
    public ResponseMessage<PermissionEntity> getPermissionById(@PathVariable UUID id) {
        return permissionService.getPermissionById(id);
    }
    
    @RequirePermission(PermissionConstants.EDITAR_PERMISO)
    @Operation(summary = "Update an existing permission")
    @PutMapping("/{id}")
    public ResponseMessage<PermissionEntity> updatePermission(@PathVariable UUID id, @RequestBody UpdatePermissionDto updatePermissionDto) {
        return permissionService.updatePermission(id, updatePermissionDto);
    }
    
    @RequirePermission(PermissionConstants.LISTAR_PERMISO)
    @Operation(summary = "Get all permissions")
    @GetMapping
    public ResponseMessage<List<PermissionEntity>> getAllPermissions() {
        return permissionService.getAllPermissions();
    }
    
    @RequirePermission(PermissionConstants.ELIMINAR_PERMISO)
    @Operation(summary = "Delete a permission by ID")
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> deletePermission(@PathVariable UUID id) {
        return permissionService.deletePermission(id);
    }
}
