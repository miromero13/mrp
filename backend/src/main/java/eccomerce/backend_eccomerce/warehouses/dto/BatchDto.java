package eccomerce.backend_eccomerce.warehouses.dto;

import eccomerce.backend_eccomerce.warehouses.enums.BatchStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BatchDto {
    public UUID id;
    public String batchNumber;
    public UUID materialId;
    public String materialCode;
    public UUID productId;
    public String productName;
    public LocalDate productionDate;
    public LocalDate expirationDate;
    public BigDecimal currentStock;
    public BatchStatus status;
    public boolean expired;
}
