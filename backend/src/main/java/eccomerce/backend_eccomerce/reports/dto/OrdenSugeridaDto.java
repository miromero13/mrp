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
public class OrdenSugeridaDto {

    private String codigo;
    private String nombre;
    private String tipo; // MATERIAL o PRODUCTO
    private BigDecimal requerido;
    private BigDecimal disponible;
    private BigDecimal ordenar;
    private LocalDate fechaNecesidad;
    private String accion; // COMPRAR o FABRICAR
    private BigDecimal costoUnitario;
    private BigDecimal costoTotal;
}