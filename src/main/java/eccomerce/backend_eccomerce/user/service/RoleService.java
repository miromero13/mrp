package eccomerce.backend_eccomerce.user.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.CreateRoleDto;
import eccomerce.backend_eccomerce.user.dto.UpdateRoleDto;
import eccomerce.backend_eccomerce.user.entity.PermissionEntity;
import eccomerce.backend_eccomerce.user.entity.RoleEntity;
import eccomerce.backend_eccomerce.user.repository.PermissionRepository;
import eccomerce.backend_eccomerce.user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    // Crear un nuevo rol
    public ResponseMessage<RoleEntity> createRole(CreateRoleDto createRoleDto) {
        try {
            // Crear una nueva entidad de rol
            RoleEntity role = new RoleEntity();
            role.nombre = createRoleDto.nombre;

            // Asociar los permisos usando UUID
            Set<PermissionEntity> permissions = new HashSet<>();
            for (UUID permissionId : createRoleDto.permissionIds) {
                PermissionEntity permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado con id: " + permissionId));
                permissions.add(permission);
            }
            role.permisos = permissions;

            roleRepository.save(role);
            return ResponseMessage.success(role, "Rol creado correctamente", 1);
        } catch (DataIntegrityViolationException ex) {
            return ResponseMessage.error("No se pudo crear el rol", "Violacion de integridad de datos: " + ex.getMostSpecificCause().getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear el rol", "Ocurrio un error al crear el rol: " + ex.getMessage(), 500);
        }
    }

    // Actualizar un rol por su UUID
    public ResponseMessage<RoleEntity> updateRole(UUID id, UpdateRoleDto updateRoleDto) {
        try {
            RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));

            if (updateRoleDto.nombre != null && !updateRoleDto.nombre.isEmpty()) {
                role.nombre = updateRoleDto.nombre;
            }

            if (updateRoleDto.permissionIds != null && !updateRoleDto.permissionIds.isEmpty()) {
                Set<PermissionEntity> permissions = new HashSet<>();
                for (UUID permissionId : updateRoleDto.permissionIds) {
                    PermissionEntity permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RuntimeException("Permiso no encontrado con id: " + permissionId));
                    permissions.add(permission);
                }
                role.permisos = permissions;
            }

            roleRepository.save(role);
            return ResponseMessage.success(role, "Rol actualizado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar el rol", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el rol", "Ocurrio un error al actualizar el rol: " + ex.getMessage(), 500);
        }
    }

    // Obtener un rol por su UUID
    public ResponseMessage<RoleEntity> getRoleById(UUID id) {
        try {
            RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));

            return ResponseMessage.success(role, "Rol encontrado", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener el rol", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener el rol", "Ocurrio un error al consultar el rol: " + ex.getMessage(), 500);
        }
    }

    // Eliminar un rol por su UUID
    public ResponseMessage<Void> deleteRole(UUID id) {
        try {
            RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));

            roleRepository.delete(role);
            return ResponseMessage.success(null, "Rol eliminado correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar el rol", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo eliminar el rol", "Ocurrio un error al eliminar el rol: " + ex.getMessage(), 500);
        }
    }

    // Obtener todos los roles
    public ResponseMessage<List<RoleEntity>> getAllRoles() {
        try {
            List<RoleEntity> roles = roleRepository.findAll();
            return ResponseMessage.success(roles, "Roles obtenidos correctamente", roles.size());
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener los roles", "Ocurrio un error al consultar roles: " + ex.getMessage(), 500);
        }
    }
}
