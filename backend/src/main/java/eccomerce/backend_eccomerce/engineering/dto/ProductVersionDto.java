package eccomerce.backend_eccomerce.engineering.dto;

import jakarta.validation.constraints.NotBlank;

public class ProductVersionDto {
    @NotBlank
    public String name;
}
