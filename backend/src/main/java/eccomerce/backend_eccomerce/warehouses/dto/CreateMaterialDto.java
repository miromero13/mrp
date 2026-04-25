package eccomerce.backend_eccomerce.warehouses.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateMaterialDto {
    @NotBlank
    public String code;

    @NotBlank
    public String unitOfMeasure;

    @NotNull
    public BigDecimal stockMin;

    @NotNull
    public BigDecimal stockCurrent;
}
