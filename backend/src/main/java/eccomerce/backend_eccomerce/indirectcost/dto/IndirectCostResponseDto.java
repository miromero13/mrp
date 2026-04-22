package eccomerce.backend_eccomerce.indirectcost.dto;

import eccomerce.backend_eccomerce.indirectcost.enums.Currency;
import eccomerce.backend_eccomerce.indirectcost.enums.DistributionCriterion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class IndirectCostResponseDto {
    private UUID id;
    private String categoryName;
    private UUID categoryId;
    private BigDecimal amount;
    private Currency currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private DistributionCriterion distributionCriterion;
    private String costCenterName;
    private UUID costCenterId;
    private Boolean active;
}
