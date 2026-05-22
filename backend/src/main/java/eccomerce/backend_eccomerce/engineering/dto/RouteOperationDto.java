package eccomerce.backend_eccomerce.engineering.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class RouteOperationDto {
    public UUID id;
    public UUID machineId;
    public String machineName;
    public Integer operationOrder;
    public String description;
    public BigDecimal setupTimeMinutes;
    public BigDecimal runTimeMinutes;
}
