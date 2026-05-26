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
public class ExceptionDto {

    private String codigo;
    private String nombre;
    private String tipo; // MATERIAL o PRODUCTO
    private String tipoExcepcion; // STOCK_BAJOS, OBSOLETO, EXCESO, RETRASO
    private BigDecimal cantidadActual;
    private BigDecimal cantidadMinima;
    private BigDecimal cantidadAfectada;
    private LocalDate fechaEsperada;
    private LocalDate fechaActual;
    private String severidad; // CRITICAL, WARNING, INFO
    private String estado; // PENDING, RESOLVED
    private String descripcion;
}