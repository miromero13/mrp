package eccomerce.backend_eccomerce.finance.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateIndirectCostDto {
    private String name;
    private String description;
    private BigDecimal amount;
}
