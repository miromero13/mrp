package eccomerce.backend_eccomerce.engineering.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class CreateProductionRouteDto {
    @NotNull
    public UUID productVersionId;

    @NotBlank
    public String name;

    public Boolean isActive = true;

    @NotEmpty
    @Valid
    public List<CreateRouteOperationDto> operations;
}
