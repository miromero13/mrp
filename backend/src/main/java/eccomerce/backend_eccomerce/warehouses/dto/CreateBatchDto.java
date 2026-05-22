package eccomerce.backend_eccomerce.warehouses.dto;

import eccomerce.backend_eccomerce.warehouses.enums.BatchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateBatchDto {
    @NotBlank
    public String batchNumber;

    public UUID materialId;

    public UUID productId;

    @NotNull
    public LocalDate productionDate;

    public LocalDate expirationDate;

    @NotNull
    @PositiveOrZero
    public BigDecimal currentStock;

    @NotNull
    public BatchStatus status;
}
