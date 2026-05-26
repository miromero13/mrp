package eccomerce.backend_eccomerce.reports.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryDto {

    private String codigo;
    private String nombre;
    private String unidadMedida;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private BigDecimal comprometido;
    private BigDecimal enOrden;
    private BigDecimal disponibleNeto;
    private BigDecimal costoUnitario;
    private BigDecimal valorTotal;
    private LocalDate ultimoMovimiento;
    private Integer diasSinMovimiento;
    private String estado; // NORMAL, BAJO, CRITICO, EXCESO
}