package eccomerce.backend_eccomerce.workcenter.dto;

import eccomerce.backend_eccomerce.workcenter.enums.ResourceTypeEnum;
import eccomerce.backend_eccomerce.workcenter.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WorkCenterResponseDto {

    private UUID id;

    private String code;

    private String name;

    private String description;

    private String plant;

    private String productionLine;

    private ResourceTypeEnum resourceType;

    private BigDecimal capacity;

    private BigDecimal costPerHour;

    private BigDecimal targetEfficiency;

    private BigDecimal currentOee;

    private Boolean isBottleneck;

    private Boolean isCriticalResource;

    private StatusEnum status;

    private String calendar;

    private LocalDate lastMaintenanceDate;

    private LocalDate nextMaintenanceDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
