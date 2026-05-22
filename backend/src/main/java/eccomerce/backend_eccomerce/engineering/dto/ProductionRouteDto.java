package eccomerce.backend_eccomerce.engineering.dto;

import java.util.List;
import java.util.UUID;

public class ProductionRouteDto {
    public UUID id;
    public UUID productVersionId;
    public String productVersionName;
    public UUID productId;
    public String productName;
    public String name;
    public boolean isActive;
    public List<RouteOperationDto> operations;
}
