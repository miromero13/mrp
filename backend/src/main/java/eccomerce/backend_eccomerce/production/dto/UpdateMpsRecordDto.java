package eccomerce.backend_eccomerce.production.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateMpsRecordDto {
    @NotNull
    public BigDecimal plannedProduction;
}
