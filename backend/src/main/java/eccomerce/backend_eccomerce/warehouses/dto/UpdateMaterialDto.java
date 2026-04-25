package eccomerce.backend_eccomerce.warehouses.dto;

import java.math.BigDecimal;

public class UpdateMaterialDto {
    public String code;
    public String unitOfMeasure;
    public BigDecimal stockMin;
    public BigDecimal stockCurrent;
}
