package eccomerce.backend_eccomerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public class UpdatePermissionDto {

    @Schema(description = "Name of the permission", example = "READ_USER")
    @Size(min = 3, max = 100, message = "Permission name must be between 3 and 100 characters")
    public String name;

    @Schema(description = "Description of the permission", example = "Allows reading user data")
    @Size(min = 10, max = 300, message = "Description must be between 10 and 300 characters")
    public String description;
}
