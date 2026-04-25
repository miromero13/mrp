package eccomerce.backend_eccomerce.users.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.enterprise.dto.EnterpriseSessionDto;
import eccomerce.backend_eccomerce.users.dto.AuthLoginResponseDto;
import eccomerce.backend_eccomerce.users.dto.PermissionSessionDto;
import eccomerce.backend_eccomerce.users.dto.RoleSessionDto;
import eccomerce.backend_eccomerce.users.dto.UserLoginRequestDto;
import eccomerce.backend_eccomerce.users.dto.UserSessionDto;
import eccomerce.backend_eccomerce.users.entity.UserEntity;
import eccomerce.backend_eccomerce.users.provider.UserJwtTokenProvider;
import eccomerce.backend_eccomerce.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserJwtTokenProvider jwtTokenProvider;

    // Método para autenticar un usuario
    public ResponseMessage<AuthLoginResponseDto> authenticateUser(UserLoginRequestDto loginRequestDto) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(loginRequestDto.email);

        if (userOptional.isEmpty()) {
            return ResponseMessage.error("No se pudo iniciar sesion", "Usuario no encontrado con el correo: " + loginRequestDto.email, 404);
        }

        UserEntity user = userOptional.get();

        // Verificar la contraseña
        if (!passwordEncoder.matches(loginRequestDto.password, user.password)) {
            return ResponseMessage.error("No se pudo iniciar sesion", "Contrasena incorrecta", 401);
        }

        // Generar JWT
        String token = jwtTokenProvider.generateToken(user);

        UserSessionDto userSessionDto = buildUserSession(user);

        AuthLoginResponseDto responseDto = new AuthLoginResponseDto();
        responseDto.access_token = token;
        responseDto.user = userSessionDto;

        return ResponseMessage.success(responseDto, "Inicio de sesion exitoso", 1);
    }

    public ResponseMessage<UserSessionDto> getCurrentSession(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseMessage.error("No se pudo obtener la sesion", "Usuario no encontrado con el correo: " + email, 404);
        }

        return ResponseMessage.success(buildUserSession(userOptional.get()), "Sesion actualizada", 1);
    }

    private UserSessionDto buildUserSession(UserEntity user) {
        UserSessionDto userSessionDto = new UserSessionDto();
        userSessionDto.id = user.getId();
        userSessionDto.name = user.name;
        userSessionDto.email = user.email;
        userSessionDto.phone = user.phone;
        userSessionDto.gender = user.gender;
        userSessionDto.address = user.address;

        if (user.enterprise != null) {
            EnterpriseSessionDto enterpriseSessionDto = new EnterpriseSessionDto();
            enterpriseSessionDto.id = user.enterprise.getId();
            enterpriseSessionDto.name = user.enterprise.getName();
            enterpriseSessionDto.nit = user.enterprise.getNit();
            enterpriseSessionDto.address = user.enterprise.getAddress();
            userSessionDto.enterprise = enterpriseSessionDto;
        }

        if (user.role != null) {
            RoleSessionDto roleSessionDto = new RoleSessionDto();
            roleSessionDto.id = user.role.getId();
            roleSessionDto.name = user.role.name;
            roleSessionDto.permissions = user.role.permissions == null ? java.util.List.of() : user.role.permissions.stream()
                    .map(permission -> {
                        PermissionSessionDto permissionSessionDto = new PermissionSessionDto();
                        permissionSessionDto.id = permission.getId();
                        permissionSessionDto.name = permission.name;
                        permissionSessionDto.description = permission.description;
                        return permissionSessionDto;
                    })
                    .collect(Collectors.toList());
            userSessionDto.role = roleSessionDto;
        }

        return userSessionDto;
    }
}
