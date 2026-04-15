package eccomerce.backend_eccomerce.user.dto;

import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public class UpdateRoleDto {
    @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
    public String name;

    public Set<UUID> permissionIds;     
}
