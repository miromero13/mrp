package eccomerce.backend_eccomerce.workcenter.dto;

import eccomerce.backend_eccomerce.workcenter.enums.ResourceTypeEnum;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateWorkCenterDto {

    @NotBlank(message = "El código es obligatorio")
    private String code;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotBlank(message = "La planta es obligatoria")
    private String plant;

    private String productionLine;

    @NotNull(message = "El tipo de recurso es obligatorio")
    private ResourceTypeEnum resourceType;

    @NotNull(message = "La capacidad es obligatoria")
    @DecimalMin("0.1")
    private BigDecimal capacity;

    @NotNull(message = "El costo por hora es obligatorio")
    @DecimalMin("0")
    private BigDecimal costPerHour;

    @NotNull(message = "La eficiencia objetivo es obligatoria")
    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal targetEfficiency;

    private Boolean isBottleneck = false;

    private Boolean isCriticalResource = false;

    private String calendar;
}
