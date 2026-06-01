package eccomerce.backend_eccomerce.reports.dto.gerencial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiReportDto {
    private int totalOrdenesSugeridas;
    private BigDecimal costoTotalOrdenes;
    private int totalAcciones;
    private long accionesUrgentes;
    private int totalExcepciones;
    private long excepcionesCriticas;
    private long materialesCriticos;
    private long materialesBajos;
    private BigDecimal valorTotalInventario;
}
