package eccomerce.backend_eccomerce.reports.service.export;

import eccomerce.backend_eccomerce.reports.dto.ActionMessageDto;
import eccomerce.backend_eccomerce.reports.dto.ExceptionDto;
import eccomerce.backend_eccomerce.reports.dto.InventorySummaryDto;
import eccomerce.backend_eccomerce.reports.dto.OrdenSugeridaDto;
import eccomerce.backend_eccomerce.reports.dto.gerencial.KpiReportDto;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public byte[] exportOrdenesSugeridas(List<OrdenSugeridaDto> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("Codigo,Nombre,Tipo,Requerido,Disponible,Ordenar,Fecha Necesidad,Accion,Costo Unitario,Costo Total\n");
        for (OrdenSugeridaDto dto : list) {
            sb.append(escape(dto.getCodigo())).append(",")
                    .append(escape(dto.getNombre())).append(",")
                    .append(escape(dto.getTipo())).append(",")
                    .append(dto.getRequerido()).append(",")
                    .append(dto.getDisponible()).append(",")
                    .append(dto.getOrdenar()).append(",")
                    .append(dto.getFechaNecesidad() != null ? dto.getFechaNecesidad().format(DATE_FORMATTER) : "").append(",")
                    .append(escape(dto.getAccion())).append(",")
                    .append(dto.getCostoUnitario()).append(",")
                    .append(dto.getCostoTotal()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportAcciones(List<ActionMessageDto> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("Codigo Item,Nombre Item,Tipo Item,Tipo Accion,Fecha Anterior,Fecha Nueva,Cantidad,Razon,Prioridad,Fecha Actual\n");
        for (ActionMessageDto dto : list) {
            sb.append(escape(dto.getCodigoItem())).append(",")
                    .append(escape(dto.getNombreItem())).append(",")
                    .append(escape(dto.getTipoItem())).append(",")
                    .append(escape(dto.getTipoAccion())).append(",")
                    .append(dto.getFechaAnterior() != null ? dto.getFechaAnterior().format(DATE_FORMATTER) : "").append(",")
                    .append(dto.getFechaNueva() != null ? dto.getFechaNueva().format(DATE_FORMATTER) : "").append(",")
                    .append(dto.getCantidadNueva()).append(",")
                    .append(escape(dto.getRazon())).append(",")
                    .append(escape(dto.getPrioridad())).append(",")
                    .append(dto.getFechaActual() != null ? dto.getFechaActual().format(DATE_FORMATTER) : "").append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportExcepciones(List<ExceptionDto> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("Codigo,Nombre,Tipo,Tipo Excepcion,Cantidad Actual,Cantidad Minima,Cantidad Afectada,Severidad,Estado,Descripcion\n");
        for (ExceptionDto dto : list) {
            sb.append(escape(dto.getCodigo())).append(",")
                    .append(escape(dto.getNombre())).append(",")
                    .append(escape(dto.getTipo())).append(",")
                    .append(escape(dto.getTipoExcepcion())).append(",")
                    .append(dto.getCantidadActual()).append(",")
                    .append(dto.getCantidadMinima()).append(",")
                    .append(dto.getCantidadAfectada()).append(",")
                    .append(escape(dto.getSeveridad())).append(",")
                    .append(escape(dto.getEstado())).append(",")
                    .append(escape(dto.getDescripcion())).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportInventario(List<InventorySummaryDto> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("Codigo,Nombre,Unidad Medida,Stock Actual,Stock Minimo,Comprometido,En Orden,Disponible Neto,Costo Unitario,Valor Total,Ultimo Movimiento,Dias Sin Movimiento,Estado\n");
        for (InventorySummaryDto dto : list) {
            sb.append(escape(dto.getCodigo())).append(",")
                    .append(escape(dto.getNombre())).append(",")
                    .append(escape(dto.getUnidadMedida())).append(",")
                    .append(dto.getStockActual()).append(",")
                    .append(dto.getStockMinimo()).append(",")
                    .append(dto.getComprometido()).append(",")
                    .append(dto.getEnOrden()).append(",")
                    .append(dto.getDisponibleNeto()).append(",")
                    .append(dto.getCostoUnitario()).append(",")
                    .append(dto.getValorTotal()).append(",")
                    .append(dto.getUltimoMovimiento() != null ? dto.getUltimoMovimiento().format(DATE_FORMATTER) : "").append(",")
                    .append(dto.getDiasSinMovimiento()).append(",")
                    .append(escape(dto.getEstado())).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportKpis(KpiReportDto dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Indicador,Valor\n");
        sb.append("Total Ordenes Sugeridas,").append(dto.getTotalOrdenesSugeridas()).append("\n");
        sb.append("Costo Total Ordenes,").append(dto.getCostoTotalOrdenes()).append("\n");
        sb.append("Total Acciones,").append(dto.getTotalAcciones()).append("\n");
        sb.append("Acciones Urgentes,").append(dto.getAccionesUrgentes()).append("\n");
        sb.append("Total Excepciones,").append(dto.getTotalExcepciones()).append("\n");
        sb.append("Excepciones Criticas,").append(dto.getExcepcionesCriticas()).append("\n");
        sb.append("Materiales Criticos,").append(dto.getMaterialesCriticos()).append("\n");
        sb.append("Materiales Bajos,").append(dto.getMaterialesBajos()).append("\n");
        sb.append("Valor Total Inventario,").append(dto.getValorTotalInventario()).append("\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
