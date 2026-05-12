package eccomerce.backend_eccomerce.warehouses.dto;

import eccomerce.backend_eccomerce.warehouses.enums.MaterialMovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CreateMaterialMovementDto {
    @NotNull
    public MaterialMovementType type;

    @NotNull
    @Positive
    public BigDecimal quantity;

    @NotNull
    @Positive
    public BigDecimal unitPrice;

    @NotBlank
    public String reason;
}
