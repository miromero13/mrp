package eccomerce.backend_eccomerce.engineering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateProductDto {
    @NotBlank
    public String name;

    public String description;

    @NotNull
    public BigDecimal productionCost;

    @NotNull
    public BigDecimal salePrice;

    @NotEmpty
    public List<UUID> materialIds;

    @NotEmpty
    public List<ProductVersionDto> versions;
}
