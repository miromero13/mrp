package eccomerce.backend_eccomerce.enterprise.dto;

import eccomerce.backend_eccomerce.common.enums.GenderEnum;
import lombok.Data;

@Data
public class CreateEnterpriseDto {
    private String name;
    private String nit;
    private String address;

    private String adminName;
    private String adminEmail;
    private String adminPassword;
    private String adminPhone;
    private GenderEnum adminGender;
    private String adminAddress;
}
