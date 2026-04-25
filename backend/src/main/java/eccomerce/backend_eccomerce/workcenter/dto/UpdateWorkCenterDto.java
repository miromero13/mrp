package eccomerce.backend_eccomerce.workcenter.dto;

import eccomerce.backend_eccomerce.workcenter.enums.ResourceTypeEnum;
import eccomerce.backend_eccomerce.workcenter.enums.StatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateWorkCenterDto {

    private String name;

    private String description;

    private String productionLine;

    private ResourceTypeEnum resourceType;

    @DecimalMin("0.1")
    private BigDecimal capacity;

    @DecimalMin("0")
    private BigDecimal costPerHour;

    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal targetEfficiency;

    private BigDecimal currentOee;

    private Boolean isBottleneck;

    private Boolean isCriticalResource;

    private StatusEnum status;

    private String calendar;

    private LocalDate nextMaintenanceDate;
}
