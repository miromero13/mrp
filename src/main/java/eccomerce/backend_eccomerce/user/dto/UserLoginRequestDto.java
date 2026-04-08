package eccomerce.backend_eccomerce.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserLoginRequestDto {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo invalido")
    public String email;

    @NotBlank(message = "La contrasena es obligatoria")
    public String password;
}
