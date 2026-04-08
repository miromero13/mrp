package eccomerce.backend_eccomerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePermissionDto {

    @Schema(description = "Name of the permission", example = "READ_USER", required = true)
    @NotBlank(message = "Permission name is required")
    @Size(min = 3, max = 100, message = "Permission name must be between 3 and 100 characters")
    public String name;

    @Schema(description = "Description of the permission", example = "Allows reading user data", required = true)
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 300, message = "Description must be between 10 and 300 characters")
    public String description;
}
