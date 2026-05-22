package eccomerce.backend_eccomerce.warehouses.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateBomItemDto {
    @NotNull
    public UUID productVersionId;

    @NotNull
    public UUID materialId;

    @NotNull
    @Digits(integer = 10, fraction = 4)
    @DecimalMin("0.0001")
    public BigDecimal quantity;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Digits(integer = 3, fraction = 2)
    public BigDecimal scrapFactor = BigDecimal.ZERO;
}
