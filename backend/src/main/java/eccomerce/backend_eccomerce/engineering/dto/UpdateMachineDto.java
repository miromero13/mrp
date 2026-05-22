package eccomerce.backend_eccomerce.engineering.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateMachineDto {
    private String serialNumber;

    private String name;
    private String description;
    private BigDecimal cost;
    private String status;
}
