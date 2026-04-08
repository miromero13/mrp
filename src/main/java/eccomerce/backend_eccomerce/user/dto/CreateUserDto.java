package eccomerce.backend_eccomerce.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import eccomerce.backend_eccomerce.common.enums.GenderEnum;

public class CreateUserDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    public String name;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is invalid")
    public String phone;

    public GenderEnum gender;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    public String address;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    public String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 12, message = "Password must be between 8 and 12 characters")
     @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    public String password;

    @NotBlank(message = "Role is required")
    public UUID roleId;
}
