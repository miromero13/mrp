package eccomerce.backend_eccomerce.user.dto;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateRoleDto {
    @NotBlank(message = "Role name is required")
    @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
    public String name;

    @NotBlank(message = "Permission ids is required")
    public Set<UUID> permissionIds;
}
