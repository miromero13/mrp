package eccomerce.backend_eccomerce.engineering.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class UpdateProductDto {
    public String name;
    public String description;
    public BigDecimal productionCost;
    public BigDecimal salePrice;
    public List<UUID> materialIds;
    public List<ProductVersionDto> versions;
}
