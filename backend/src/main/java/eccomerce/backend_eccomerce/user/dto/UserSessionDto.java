package eccomerce.backend_eccomerce.user.dto;

import java.util.UUID;

import eccomerce.backend_eccomerce.common.enums.GenderEnum;

public class UserSessionDto {
    public UUID id;
    public String name;
    public String email;
    public String phone;
    public GenderEnum gender;
    public String address;
    public RoleSessionDto role;
}
