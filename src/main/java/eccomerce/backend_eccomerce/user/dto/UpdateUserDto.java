package eccomerce.backend_eccomerce.user.dto;

import eccomerce.backend_eccomerce.common.enums.GenderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class UpdateUserDto {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    public String name;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    public String phone;

    public GenderEnum gender;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    public String address;

    @Email(message = "Email should be valid")
    public String email;

    @Size(min = 8, max = 12, message = "Password must be between 8 and 12 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    public String password;

    public UUID roleId;

}
