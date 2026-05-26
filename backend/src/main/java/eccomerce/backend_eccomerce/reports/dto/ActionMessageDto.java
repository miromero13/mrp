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
public class ActionMessageDto {

    private String codigoItem;
    private String nombreItem;
    private String tipoItem;
    private String tipoAccion; // ADELANTAR, RETRASAR, CANCELAR, AUMENTAR, DISMINUIR
    private LocalDate fechaAnterior;
    private LocalDate fechaNueva;
    private BigDecimal cantidadAnterior;
    private BigDecimal cantidadNueva;
    private String razon;
    private String prioridad; // HIGH, MEDIUM, LOW
    private LocalDate fechaActual;
}