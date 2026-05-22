package eccomerce.backend_eccomerce.engineering.dto;

import jakarta.validation.Valid;

import java.util.List;

public class UpdateProductionRouteDto {
    public String name;
    public Boolean isActive;

    @Valid
    public List<CreateRouteOperationDto> operations;
}
