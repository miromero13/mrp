package eccomerce.backend_eccomerce.materials.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BatchDto {
    private UUID id;
    private String batchNumber;
    private LocalDate productionDate;
    private LocalDate expirationDate;
    private BigDecimal currentStock;
    private UUID materialId;
    private UUID productId;
    private UUID enterpriseId;
    private String status;
}
