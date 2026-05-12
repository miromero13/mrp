package eccomerce.backend_eccomerce.finance.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateIndirectCostDto {
    private String name;
    private String description;
    private BigDecimal amount;
}
