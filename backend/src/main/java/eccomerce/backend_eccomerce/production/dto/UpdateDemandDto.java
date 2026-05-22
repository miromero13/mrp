package eccomerce.backend_eccomerce.production.dto;

import eccomerce.backend_eccomerce.production.enums.DemandStatus;
import eccomerce.backend_eccomerce.production.enums.DemandType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class UpdateDemandDto {
    public UUID productId;
    public DemandType type;

    @Digits(integer = 10, fraction = 2)
    @DecimalMin("0.01")
    public BigDecimal quantity;

    public LocalDate dueDate;
    public DemandStatus status;
}
