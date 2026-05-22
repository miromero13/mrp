package eccomerce.backend_eccomerce.production.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class MpsRecordDto {
    public UUID id;
    public UUID enterpriseId;
    public String enterpriseName;
    public UUID productId;
    public String productName;
    public eccomerce.backend_eccomerce.production.enums.MpsPeriodMode periodMode;
    public LocalDate periodStartDate;
    public BigDecimal forecastDemand;
    public BigDecimal allocatedDemand;
    public BigDecimal plannedProduction;
    public BigDecimal projectedAvailableBalance;
}
