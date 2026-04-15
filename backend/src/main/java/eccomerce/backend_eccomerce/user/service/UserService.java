package eccomerce.backend_eccomerce.user.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.CreateUserDto;
import eccomerce.backend_eccomerce.user.dto.UpdateUserDto;
import eccomerce.backend_eccomerce.user.entity.RoleEntity;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.user.repository.RoleRepository;
import eccomerce.backend_eccomerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Crear un nuevo usuario
    public ResponseMessage<UserEntity> createUser(CreateUserDto createUserDto) {
        try {
            // Crear una nueva entidad de usuario
            UserEntity user = new UserEntity();
            user.name = createUserDto.name;
            user.phone = createUserDto.phone;
            user.gender = createUserDto.gender;
            user.address = createUserDto.address;
            user.email = createUserDto.email;
            
            // Encriptar la contraseña
            user.password = passwordEncoder.encode(createUserDto.password);

            // Buscar el rol por ID
            RoleEntity role = roleRepository.findById(createUserDto.roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + createUserDto.roleId));
            user.role = role;

            userRepository.save(user);
            return ResponseMessage.success(user, "Usuario creado correctamente", 1);
        } catch (DataIntegrityViolationException ex) {
            return ResponseMessage.error("No se pudo crear el usuario", "Violacion de integridad de datos: " + ex.getMostSpecificCause().getMessage(), 400);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo crear el usuario", "Ocurrio un error al crear el usuario: " + ex.getMessage(), 500);
        }
    }

    // Actualizar un usuario por su UUID
    public ResponseMessage<UserEntity> updateUser(UUID id, UpdateUserDto updateUserDto) {
        try {
            UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

            if (updateUserDto.name != null && !updateUserDto.name.isEmpty()) {
                user.name = updateUserDto.name;
            }
            if (updateUserDto.phone != null && !updateUserDto.phone.isEmpty()) {
                user.phone = updateUserDto.phone;
            }
            if (updateUserDto.gender != null) {
                user.gender = updateUserDto.gender;
            }
            if (updateUserDto.address != null && !updateUserDto.address.isEmpty()) {
                user.address = updateUserDto.address;
            }
            if (updateUserDto.email != null && !updateUserDto.email.isEmpty()) {
                user.email = updateUserDto.email;
            }
            if (updateUserDto.password != null && !updateUserDto.password.isEmpty()) {
                // Encriptar la nueva contraseña
                user.password = passwordEncoder.encode(updateUserDto.password);
            }
            if (updateUserDto.roleId != null) {
                RoleEntity role = roleRepository.findById(updateUserDto.roleId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + updateUserDto.roleId));
                user.role = role;
            }

            userRepository.save(user);
            return ResponseMessage.success(user, "Usuario actualizado correctamente", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo actualizar el usuario", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo actualizar el usuario", "Ocurrio un error al actualizar el usuario: " + ex.getMessage(), 500);
        }
    }

    // Obtener un usuario por su UUID
    public ResponseMessage<UserEntity> getUserById(UUID id) {
        try {
            UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

            return ResponseMessage.success(user, "Usuario encontrado", 1);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo obtener el usuario", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo obtener el usuario", "Ocurrio un error al consultar el usuario: " + ex.getMessage(), 500);
        }
    }

    // Eliminar un usuario por su UUID
    public ResponseMessage<Void> deleteUser(UUID id) {
        try {
            UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

            userRepository.delete(user);
            return ResponseMessage.success(null, "Usuario eliminado correctamente", null);
        } catch (RuntimeException ex) {
            return ResponseMessage.error("No se pudo eliminar el usuario", ex.getMessage(), 404);
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudo eliminar el usuario", "Ocurrio un error al eliminar el usuario: " + ex.getMessage(), 500);
        }
    }

    // Obtener todos los usuarios
    public ResponseMessage<List<UserEntity>> getAllUsers() {
        try {
            List<UserEntity> users = userRepository.findAll();
            return ResponseMessage.success(users, "Usuarios obtenidos correctamente", users.size());
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener los usuarios", "Ocurrio un error al consultar usuarios: " + ex.getMessage(), 500);
        }
    }
}
