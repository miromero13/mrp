package eccomerce.backend_eccomerce.user.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.CreatePermissionDto;
import eccomerce.backend_eccomerce.user.dto.UpdatePermissionDto;
import eccomerce.backend_eccomerce.user.entity.PermissionEntity;
import eccomerce.backend_eccomerce.user.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public ResponseMessage<PermissionEntity> createPermission(CreatePermissionDto createPermissionDto) {
        try {
            PermissionEntity permission = new PermissionEntity();
            permission.name = createPermissionDto.name;
            permission.description = createPermissionDto.description;
            permissionRepository.save(permission);

            return ResponseMessage.success(permission, "Permiso creado correctamente", 1);
        } catch (DataIntegrityViolationException ex) {            
            return ResponseMessage.error("No se pudo crear el permiso", "Violacion de integridad de datos: " + ex.getMostSpecificCause().getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear el permiso", "Ocurrio un error al crear el permiso: " + ex.getMessage(), 500);
        }
    }

    public ResponseMessage<PermissionEntity> updatePermission(@NonNull UUID id, UpdatePermissionDto updatePermissionDto) {
        try {
            PermissionEntity permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con id: " + id));

            if (updatePermissionDto.name != null && !updatePermissionDto.name.isEmpty()) {
                permission.name = updatePermissionDto.name;
            }
            if (updatePermissionDto.description != null && !updatePermissionDto.description.isEmpty()) {
                permission.description = updatePermissionDto.description;
            }
            permissionRepository.save(permission);

            return ResponseMessage.success(permission, "Permiso actualizado correctamente", 1);
        } catch (RuntimeException ex) {            
            return ResponseMessage.error("No se pudo actualizar el permiso", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el permiso", "Ocurrio un error al actualizar el permiso: " + ex.getMessage(), 500);
        }
    }

    public ResponseMessage<PermissionEntity> getPermissionById(@NonNull UUID id) {
        try {
            PermissionEntity permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con id: " + id));

            return ResponseMessage.success(permission, "Permiso encontrado", 1);
        } catch (RuntimeException ex) {            
            return ResponseMessage.error("No se pudo obtener el permiso", ex.getMessage(), 404);
        } catch (Exception ex) {            
            return ResponseMessage.error("No se pudo obtener el permiso", "Ocurrio un error al consultar el permiso: " + ex.getMessage(), 500);
        }
    }

    public ResponseMessage<Void> deletePermission(@NonNull UUID id) {
        try {
            PermissionEntity permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con id: " + id));

            permissionRepository.delete(permission);

            return ResponseMessage.success(null, "Permiso eliminado correctamente", null);
        } catch (RuntimeException ex) {            
            return ResponseMessage.error("No se pudo eliminar el permiso", ex.getMessage(), 404);
        } catch (Exception ex) {            
            return ResponseMessage.error("No se pudo eliminar el permiso", "Ocurrio un error al eliminar el permiso: " + ex.getMessage(), 500);
        }
    }

    public ResponseMessage<List<PermissionEntity>> getAllPermissions() {
        try {
            List<PermissionEntity> permissions = permissionRepository.findAll();
            return ResponseMessage.success(permissions, "Permisos obtenidos correctamente", permissions.size());
        } catch (Exception ex) {            
            return ResponseMessage.error("No se pudieron obtener los permisos", "Ocurrio un error al consultar permisos: " + ex.getMessage(), 500);
        }
    }
}
