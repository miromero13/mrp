package eccomerce.backend_eccomerce.production.dto;

import eccomerce.backend_eccomerce.production.enums.DemandType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateDemandDto {
    @NotNull
    public UUID productId;

    @NotNull
    public DemandType type;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @DecimalMin("0.01")
    public BigDecimal quantity;

    @NotNull
    public LocalDate dueDate;
}
