package eccomerce.backend_eccomerce.user.service;

import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import eccomerce.backend_eccomerce.user.dto.UserLoginRequestDto;
import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.user.provider.UserJwtTokenProvider;
import eccomerce.backend_eccomerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserJwtTokenProvider jwtTokenProvider;

    // Método para autenticar un usuario
    public ResponseMessage<String> authenticateUser(UserLoginRequestDto loginRequestDto) {
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

        return ResponseMessage.success(token, "Inicio de sesion exitoso", 1);
    }
}
