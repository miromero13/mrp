package eccomerce.backend_eccomerce.user.dto;

import java.util.List;
import java.util.UUID;

public class RoleSessionDto {
    public UUID id;
    public String name;
    public List<PermissionSessionDto> permissions;
}
