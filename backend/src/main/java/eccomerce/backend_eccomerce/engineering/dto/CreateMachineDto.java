package eccomerce.backend_eccomerce.engineering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateMachineDto {
    @NotBlank
    private String serialNumber;

    @NotBlank
    private String name;
    private String description;

    @NotNull
    private BigDecimal cost;
    private String status;
}
