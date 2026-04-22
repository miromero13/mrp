package eccomerce.backend_eccomerce.indirectcost.dto;

import eccomerce.backend_eccomerce.indirectcost.enums.Currency;
import eccomerce.backend_eccomerce.indirectcost.enums.DistributionCriterion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class IndirectCostRequestDto {
    @NotNull(message = "La categoría es obligatoria")
    private UUID categoryId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal amount;

    @NotNull(message = "La moneda es obligatoria")
    private Currency currency;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    @NotNull(message = "El criterio de distribución es obligatorio")
    private DistributionCriterion distributionCriterion;

    @NotNull(message = "El centro de costo es obligatorio")
    private UUID costCenterId;
}
