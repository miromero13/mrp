package eccomerce.backend_eccomerce.user.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.CreateRoleDto;
import eccomerce.backend_eccomerce.user.dto.UpdateRoleDto;
import eccomerce.backend_eccomerce.user.entity.RoleEntity;
import eccomerce.backend_eccomerce.user.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "API for managing roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequirePermission(PermissionConstants.CREAR_ROL)
    @Operation(summary = "Create a new role")
    @PostMapping
    public ResponseEntity<ResponseMessage<RoleEntity>> createRole(@RequestBody CreateRoleDto createRoleDto) {
        return ResponseEntity.ok(roleService.createRole(createRoleDto));
    }

    @RequirePermission(PermissionConstants.EDITAR_ROL)
    @Operation(summary = "Update an existing role")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage<RoleEntity>> updateRole(@PathVariable UUID id, @RequestBody UpdateRoleDto updateRoleDto) {
        return ResponseEntity.ok(roleService.updateRole(id, updateRoleDto));
    }

    @RequirePermission(PermissionConstants.LISTAR_ROL)
    @Operation(summary = "Get a role by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<RoleEntity>> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @RequirePermission(PermissionConstants.ELIMINAR_ROL)
    @Operation(summary = "Delete a role by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage<Void>> deleteRole(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }

    @RequirePermission(PermissionConstants.LISTAR_ROL)
    @Operation(summary = "Get all roles")
    @GetMapping
    public ResponseEntity<ResponseMessage<List<RoleEntity>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
