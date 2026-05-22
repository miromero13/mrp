package eccomerce.backend_eccomerce.engineering.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateRouteOperationDto {
    public UUID machineId;

    @NotNull
    @Min(1)
    public Integer operationOrder;

    @NotBlank
    public String description;

    @NotNull
    @Digits(integer = 6, fraction = 2)
    @DecimalMin("0.00")
    public BigDecimal setupTimeMinutes;

    @NotNull
    @Digits(integer = 6, fraction = 2)
    @DecimalMin("0.00")
    public BigDecimal runTimeMinutes;
}
