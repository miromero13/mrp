package eccomerce.backend_eccomerce.user.service;

import eccomerce.backend_eccomerce.common.constants.RoleConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.enterprise.entity.EnterpriseEntity;
import eccomerce.backend_eccomerce.enterprise.repository.EnterpriseRepository;
import eccomerce.backend_eccomerce.user.dto.CreateUserDto;
import eccomerce.backend_eccomerce.user.dto.CreateUserWorkShiftDto;
import eccomerce.backend_eccomerce.user.dto.UpdateUserDto;
import eccomerce.backend_eccomerce.user.entity.RoleEntity;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.user.repository.RoleRepository;
import eccomerce.backend_eccomerce.user.repository.UserRepository;
import eccomerce.backend_eccomerce.work_shift.entity.EmployeeShiftEntity;
import eccomerce.backend_eccomerce.work_shift.entity.WorkShiftEntity;
import eccomerce.backend_eccomerce.work_shift.repository.EmployeeShiftRepository;
import eccomerce.backend_eccomerce.work_shift.repository.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private WorkShiftRepository workShiftRepository;

    @Autowired
    private EmployeeShiftRepository employeeShiftRepository;

    // Crear un nuevo usuario
    @Transactional
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

            if (!isCurrentUserSuperadmin() && RoleConstants.SUPERADMIN.equalsIgnoreCase(role.name)) {
                return ResponseMessage.error("No se pudo crear el usuario", "No puedes asignar el rol superadmin", 403);
            }
            if (isCurrentUserSuperadmin() && RoleConstants.EMPLOYEE.equalsIgnoreCase(role.name)) {
                return ResponseMessage.error("No se pudo crear el usuario", "Superadmin no tiene acceso a horarios de empleados", 403);
            }
            user.role = role;

            if (!isCurrentUserSuperadmin()) {
                UUID currentEnterpriseId = getCurrentEnterpriseId();
                if (currentEnterpriseId == null) {
                    return ResponseMessage.error("No se pudo crear el usuario", "Debes tener una empresa asociada", 400);
                }

                if (createUserDto.enterpriseId != null && !currentEnterpriseId.equals(createUserDto.enterpriseId)) {
                    return ResponseMessage.error("No se pudo crear el usuario", "No puedes asignar usuarios fuera de tu empresa", 403);
                }
            }

            EnterpriseEntity enterprise = resolveEnterprise(createUserDto.enterpriseId);
            if (!isCurrentUserSuperadmin() && enterprise == null) {
                return ResponseMessage.error("No se pudo crear el usuario", "Debes tener una empresa asociada", 400);
            }
            user.enterprise = enterprise;

            userRepository.save(user);

            if (RoleConstants.EMPLOYEE.equalsIgnoreCase(role.name)) {
                if (createUserDto.workShiftAssignments == null || createUserDto.workShiftAssignments.isEmpty()) {
                    return ResponseMessage.error("No se pudo crear el usuario", "Debes asignar horarios de trabajo al empleado", 400);
                }

                createEmployeeShifts(user, createUserDto.workShiftAssignments);
            }

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

            if (!canAccessUser(user)) {
                return ResponseMessage.error("No se pudo actualizar el usuario", "No puedes modificar usuarios fuera de tu empresa", 403);
            }

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

                if (!isCurrentUserSuperadmin() && RoleConstants.SUPERADMIN.equalsIgnoreCase(role.name)) {
                    return ResponseMessage.error("No se pudo actualizar el usuario", "No puedes asignar el rol superadmin", 403);
                }

                user.role = role;
            }

            if (updateUserDto.enterpriseId != null) {
                if (!isCurrentUserSuperadmin()) {
                    return ResponseMessage.error("No se pudo actualizar el usuario", "No puedes mover usuarios entre empresas", 403);
                }

                user.enterprise = enterpriseRepository.findById(updateUserDto.enterpriseId)
                        .orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + updateUserDto.enterpriseId));
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

            if (!canAccessUser(user)) {
                return ResponseMessage.error("No se pudo obtener el usuario", "No puedes ver usuarios fuera de tu empresa", 403);
            }

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

            if (!canAccessUser(user)) {
                return ResponseMessage.error("No se pudo eliminar el usuario", "No puedes eliminar usuarios fuera de tu empresa", 403);
            }

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
            UUID currentEnterpriseId = getCurrentEnterpriseId();
            List<UserEntity> users = isCurrentUserSuperadmin()
                    ? userRepository.findAll()
                    : currentEnterpriseId == null ? List.of() : userRepository.findByEnterpriseId(currentEnterpriseId);
            return ResponseMessage.success(users, "Usuarios obtenidos correctamente", users.size());
        } catch (Exception ex) {
            return ResponseMessage.error("No se pudieron obtener los usuarios", "Ocurrio un error al consultar usuarios: " + ex.getMessage(), 500);
        }
    }

    private boolean canAccessUser(UserEntity targetUser) {
        if (isCurrentUserSuperadmin()) {
            return true;
        }

        UUID currentEnterpriseId = getCurrentEnterpriseId();
        UUID targetEnterpriseId = targetUser.enterprise == null ? null : targetUser.enterprise.getId();
        return currentEnterpriseId != null && currentEnterpriseId.equals(targetEnterpriseId)
                && (targetUser.role == null || !RoleConstants.SUPERADMIN.equalsIgnoreCase(targetUser.role.name));
    }

    private EnterpriseEntity resolveEnterprise(UUID enterpriseId) {
        if (enterpriseId != null) {
            if (!isCurrentUserSuperadmin()) {
                UUID currentEnterpriseId = getCurrentEnterpriseId();
                if (currentEnterpriseId == null || !currentEnterpriseId.equals(enterpriseId)) {
                    throw new RuntimeException("No puedes asignar usuarios fuera de tu empresa");
                }
            }

            return enterpriseRepository.findById(enterpriseId)
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + enterpriseId));
        }

        if (isCurrentUserSuperadmin()) {
            return null;
        }

        UUID currentEnterpriseId = getCurrentEnterpriseId();
        if (currentEnterpriseId == null) {
            return null;
        }

        return enterpriseRepository.findById(currentEnterpriseId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    }

    private boolean isCurrentUserSuperadmin() {
        String email = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null) {
            return false;
        }

        return userRepository.findByEmail(email)
                .map(user -> user.role != null && RoleConstants.SUPERADMIN.equalsIgnoreCase(user.role.name))
                .orElse(false);
    }

    private UUID getCurrentEnterpriseId() {
        String email = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null) {
            return null;
        }

        return userRepository.findByEmail(email)
                .map(user -> user.enterprise == null ? null : user.enterprise.getId())
                .orElse(null);
    }

    private void createEmployeeShifts(UserEntity user, List<CreateUserWorkShiftDto> assignments) {
        for (CreateUserWorkShiftDto assignmentDto : assignments) {
            if (assignmentDto.workShiftId == null || assignmentDto.dayOfWeek == null || assignmentDto.dayOfWeek.isBlank()) {
                throw new RuntimeException("Cada horario debe incluir turno y día");
            }

            WorkShiftEntity workShift = workShiftRepository.findById(assignmentDto.workShiftId)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado con id: " + assignmentDto.workShiftId));

            if (!isCurrentUserSuperadmin()) {
                UUID currentEnterpriseId = getCurrentEnterpriseId();
                UUID workShiftEnterpriseId = workShift.getEnterprise() == null ? null : workShift.getEnterprise().getId();
                if (currentEnterpriseId == null || workShiftEnterpriseId == null || !currentEnterpriseId.equals(workShiftEnterpriseId)) {
                    throw new RuntimeException("No puedes asignar horarios fuera de tu empresa");
                }
            }

            EmployeeShiftEntity employeeShift = new EmployeeShiftEntity();
            employeeShift.setUser(user);
            employeeShift.setWorkShift(workShift);
            employeeShift.setDayOfWeek(assignmentDto.dayOfWeek.trim().toUpperCase());
            employeeShiftRepository.save(employeeShift);
        }
    }
}
