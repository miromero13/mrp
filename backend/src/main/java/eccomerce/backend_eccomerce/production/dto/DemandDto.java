package eccomerce.backend_eccomerce.production.dto;

import eccomerce.backend_eccomerce.production.enums.DemandStatus;
import eccomerce.backend_eccomerce.production.enums.DemandType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class DemandDto {
    public UUID id;
    public UUID enterpriseId;
    public String enterpriseName;
    public UUID productId;
    public String productName;
    public DemandType type;
    public BigDecimal quantity;
    public LocalDate dueDate;
    public DemandStatus status;
}
