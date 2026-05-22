package eccomerce.backend_eccomerce.warehouses.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class BomItemDto {
    public UUID id;
    public UUID productVersionId;
    public String productVersionName;
    public boolean approved;
    public boolean usedInProduction;
    public UUID materialId;
    public String materialCode;
    public String materialUnitOfMeasure;
    public BigDecimal materialUnitPrice;
    public BigDecimal quantity;
    public BigDecimal scrapFactor;
    public BigDecimal estimatedCost;
}
