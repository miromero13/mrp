package eccomerce.backend_eccomerce.engineering.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateRouteOperationDto {
    public UUID machineId;

    @Min(1)
    public Integer operationOrder;

    public String description;

    @Digits(integer = 6, fraction = 2)
    @DecimalMin("0.00")
    public BigDecimal setupTimeMinutes;

    @Digits(integer = 6, fraction = 2)
    @DecimalMin("0.00")
    public BigDecimal runTimeMinutes;
}
