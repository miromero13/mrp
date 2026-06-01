package eccomerce.backend_eccomerce.reports.service.export;

import eccomerce.backend_eccomerce.reports.dto.ActionMessageDto;
import eccomerce.backend_eccomerce.reports.dto.ExceptionDto;
import eccomerce.backend_eccomerce.reports.dto.InventorySummaryDto;
import eccomerce.backend_eccomerce.reports.dto.OrdenSugeridaDto;
import eccomerce.backend_eccomerce.reports.dto.gerencial.KpiReportDto;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PdfExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public byte[] exportOrdenesSugeridas(List<OrdenSugeridaDto> list) throws DRException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Estilos
        StyleBuilder titleStyle = DynamicReports.stl.style()
                .bold()
                .setFontSize(18)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setForegroundColor(new Color(10, 37, 64)); // Dark Blue

        StyleBuilder headerStyle = DynamicReports.stl.style()
                .bold()
                .setBackgroundColor(new Color(10, 37, 64))
                .setForegroundColor(Color.WHITE)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        StyleBuilder columnStyle = DynamicReports.stl.style()
                .setBorder(DynamicReports.stl.penThin())
                .setFontSize(9);

        // Mapear DTOs a objetos planos compatibles con JasperReports/DynamicReports
        List<Map<String, Object>> data = new ArrayList<>();
        for (OrdenSugeridaDto o : list) {
            data.add(Map.of(
                    "codigo", o.getCodigo() != null ? o.getCodigo() : "",
                    "nombre", o.getNombre() != null ? o.getNombre() : "",
                    "tipo", o.getTipo() != null ? o.getTipo() : "",
                    "requerido", o.getRequerido() != null ? o.getRequerido().toString() : "0",
                    "disponible", o.getDisponible() != null ? o.getDisponible().toString() : "0",
                    "ordenar", o.getOrdenar() != null ? o.getOrdenar().toString() : "0",
                    "fecha", o.getFechaNecesidad() != null ? o.getFechaNecesidad().format(DATE_FORMATTER) : "",
                    "accion", o.getAccion() != null ? o.getAccion() : "",
                    "costoTotal", o.getCostoTotal() != null ? o.getCostoTotal().toString() : "0"
            ));
        }

        TextColumnBuilder<String> colCodigo = DynamicReports.col.column("Código", "codigo", DynamicReports.type.stringType());
        TextColumnBuilder<String> colNombre = DynamicReports.col.column("Nombre", "nombre", DynamicReports.type.stringType());
        TextColumnBuilder<String> colTipo = DynamicReports.col.column("Tipo", "tipo", DynamicReports.type.stringType());
        TextColumnBuilder<String> colReq = DynamicReports.col.column("Req.", "requerido", DynamicReports.type.stringType());
        TextColumnBuilder<String> colDisp = DynamicReports.col.column("Disp.", "disponible", DynamicReports.type.stringType());
        TextColumnBuilder<String> colOrd = DynamicReports.col.column("Ordenar", "ordenar", DynamicReports.type.stringType());
        TextColumnBuilder<String> colFecha = DynamicReports.col.column("Fecha", "fecha", DynamicReports.type.stringType());
        TextColumnBuilder<String> colAccion = DynamicReports.col.column("Acción", "accion", DynamicReports.type.stringType());
        TextColumnBuilder<String> colCosto = DynamicReports.col.column("Costo Total", "costoTotal", DynamicReports.type.stringType());

        JasperReportBuilder report = DynamicReports.report()
                .setTemplate(DynamicReports.template())
                .setColumnTitleStyle(headerStyle)
                .setColumnStyle(columnStyle)
                .columns(colCodigo, colNombre, colTipo, colReq, colDisp, colOrd, colFecha, colAccion, colCosto)
                .title(DynamicReports.cmp.text("Reporte Analítico - Órdenes Sugeridas (MRP)")
                        .setStyle(titleStyle))
                .pageFooter(DynamicReports.cmp.pageXofY())
                .setDataSource(new JRBeanCollectionDataSource(data));

        report.toPdf(out);
        return out.toByteArray();
    }

    public byte[] exportAcciones(List<ActionMessageDto> list) throws DRException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        StyleBuilder titleStyle = DynamicReports.stl.style()
                .bold()
                .setFontSize(18)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setForegroundColor(new Color(10, 37, 64));

        StyleBuilder headerStyle = DynamicReports.stl.style()
                .bold()
                .setBackgroundColor(new Color(10, 37, 64))
                .setForegroundColor(Color.WHITE)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        StyleBuilder columnStyle = DynamicReports.stl.style()
                .setBorder(DynamicReports.stl.penThin())
                .setFontSize(8);

        List<Map<String, Object>> data = new ArrayList<>();
        for (ActionMessageDto a : list) {
            data.add(Map.of(
                    "codigo", a.getCodigoItem() != null ? a.getCodigoItem() : "",
                    "nombre", a.getNombreItem() != null ? a.getNombreItem() : "",
                    "tipo", a.getTipoItem() != null ? a.getTipoItem() : "",
                    "accion", a.getTipoAccion() != null ? a.getTipoAccion() : "",
                    "fechaAnt", a.getFechaAnterior() != null ? a.getFechaAnterior().format(DATE_FORMATTER) : "",
                    "fechaNue", a.getFechaNueva() != null ? a.getFechaNueva().format(DATE_FORMATTER) : "",
                    "cantidad", a.getCantidadNueva() != null ? a.getCantidadNueva().toString() : "0",
                    "razon", a.getRazon() != null ? a.getRazon() : "",
                    "prioridad", a.getPrioridad() != null ? a.getPrioridad() : ""
            ));
        }

        TextColumnBuilder<String> colCodigo = DynamicReports.col.column("Código", "codigo", DynamicReports.type.stringType());
        TextColumnBuilder<String> colNombre = DynamicReports.col.column("Nombre", "nombre", DynamicReports.type.stringType());
        TextColumnBuilder<String> colTipo = DynamicReports.col.column("Tipo", "tipo", DynamicReports.type.stringType());
        TextColumnBuilder<String> colAccion = DynamicReports.col.column("Acción", "accion", DynamicReports.type.stringType());
        TextColumnBuilder<String> colFechaA = DynamicReports.col.column("F. Anterior", "fechaAnt", DynamicReports.type.stringType());
        TextColumnBuilder<String> colFechaN = DynamicReports.col.column("F. Nueva", "fechaNue", DynamicReports.type.stringType());
        TextColumnBuilder<String> colCant = DynamicReports.col.column("Cant.", "cantidad", DynamicReports.type.stringType());
        TextColumnBuilder<String> colRazon = DynamicReports.col.column("Razón", "razon", DynamicReports.type.stringType());
        TextColumnBuilder<String> colPrio = DynamicReports.col.column("Prioridad", "prioridad", DynamicReports.type.stringType());

        JasperReportBuilder report = DynamicReports.report()
                .setColumnTitleStyle(headerStyle)
                .setColumnStyle(columnStyle)
                .columns(colCodigo, colNombre, colTipo, colAccion, colFechaA, colFechaN, colCant, colRazon, colPrio)
                .title(DynamicReports.cmp.text("Reporte Analítico - Mensajes de Acción (MRP)")
                        .setStyle(titleStyle))
                .pageFooter(DynamicReports.cmp.pageXofY())
                .setDataSource(new JRBeanCollectionDataSource(data));

        report.toPdf(out);
        return out.toByteArray();
    }

    public byte[] exportExcepciones(List<ExceptionDto> list) throws DRException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        StyleBuilder titleStyle = DynamicReports.stl.style()
                .bold()
                .setFontSize(18)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setForegroundColor(new Color(10, 37, 64));

        StyleBuilder headerStyle = DynamicReports.stl.style()
                .bold()
                .setBackgroundColor(new Color(10, 37, 64))
                .setForegroundColor(Color.WHITE)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        StyleBuilder columnStyle = DynamicReports.stl.style()
                .setBorder(DynamicReports.stl.penThin())
                .setFontSize(8);

        List<Map<String, Object>> data = new ArrayList<>();
        for (ExceptionDto e : list) {
            data.add(Map.of(
                    "codigo", e.getCodigo() != null ? e.getCodigo() : "",
                    "nombre", e.getNombre() != null ? e.getNombre() : "",
                    "tipo", e.getTipo() != null ? e.getTipo() : "",
                    "tipoExc", e.getTipoExcepcion() != null ? e.getTipoExcepcion() : "",
                    "cantAct", e.getCantidadActual() != null ? e.getCantidadActual().toString() : "0",
                    "cantMin", e.getCantidadMinima() != null ? e.getCantidadMinima().toString() : "0",
                    "cantAf", e.getCantidadAfectada() != null ? e.getCantidadAfectada().toString() : "0",
                    "severidad", e.getSeveridad() != null ? e.getSeveridad() : "",
                    "estado", e.getEstado() != null ? e.getEstado() : "",
                    "desc", e.getDescripcion() != null ? e.getDescripcion() : ""
            ));
        }

        TextColumnBuilder<String> colCodigo = DynamicReports.col.column("Código", "codigo", DynamicReports.type.stringType());
        TextColumnBuilder<String> colNombre = DynamicReports.col.column("Nombre", "nombre", DynamicReports.type.stringType());
        TextColumnBuilder<String> colTipo = DynamicReports.col.column("Tipo", "tipo", DynamicReports.type.stringType());
        TextColumnBuilder<String> colTipoE = DynamicReports.col.column("Excepción", "tipoExc", DynamicReports.type.stringType());
        TextColumnBuilder<String> colCantAct = DynamicReports.col.column("Cant. Act.", "cantAct", DynamicReports.type.stringType());
        TextColumnBuilder<String> colCantMin = DynamicReports.col.column("Cant. Mín.", "cantMin", DynamicReports.type.stringType());
        TextColumnBuilder<String> colCantAf = DynamicReports.col.column("Cant. Af.", "cantAf", DynamicReports.type.stringType());
        TextColumnBuilder<String> colSev = DynamicReports.col.column("Severidad", "severidad", DynamicReports.type.stringType());
        TextColumnBuilder<String> colEst = DynamicReports.col.column("Estado", "estado", DynamicReports.type.stringType());
        TextColumnBuilder<String> colDesc = DynamicReports.col.column("Descripción", "desc", DynamicReports.type.stringType());

        JasperReportBuilder report = DynamicReports.report()
                .setColumnTitleStyle(headerStyle)
                .setColumnStyle(columnStyle)
                .columns(colCodigo, colNombre, colTipo, colTipoE, colCantAct, colCantMin, colCantAf, colSev, colEst, colDesc)
                .title(DynamicReports.cmp.text("Reporte Analítico - Excepciones (MRP)")
                        .setStyle(titleStyle))
                .pageFooter(DynamicReports.cmp.pageXofY())
                .setDataSource(new JRBeanCollectionDataSource(data));

        report.toPdf(out);
        return out.toByteArray();
    }

    public byte[] exportInventario(List<InventorySummaryDto> list) throws DRException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        StyleBuilder titleStyle = DynamicReports.stl.style()
                .bold()
                .setFontSize(18)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setForegroundColor(new Color(10, 37, 64));

        StyleBuilder headerStyle = DynamicReports.stl.style()
                .bold()
                .setBackgroundColor(new Color(10, 37, 64))
                .setForegroundColor(Color.WHITE)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        StyleBuilder columnStyle = DynamicReports.stl.style()
                .setBorder(DynamicReports.stl.penThin())
                .setFontSize(7);

        List<Map<String, Object>> data = new ArrayList<>();
        for (InventorySummaryDto i : list) {
            data.add(createInventoryMap(i));
        }

        TextColumnBuilder<String> colCodigo = DynamicReports.col.column("Código", "codigo", DynamicReports.type.stringType());
        TextColumnBuilder<String> colNombre = DynamicReports.col.column("Nombre", "nombre", DynamicReports.type.stringType());
        TextColumnBuilder<String> colUm = DynamicReports.col.column("U.M.", "um", DynamicReports.type.stringType());
        TextColumnBuilder<String> colStockAct = DynamicReports.col.column("Stock Act.", "stockAct", DynamicReports.type.stringType());
        TextColumnBuilder<String> colStockMin = DynamicReports.col.column("Stock Mín.", "stockMin", DynamicReports.type.stringType());
        TextColumnBuilder<String> colComp = DynamicReports.col.column("Compr.", "comp", DynamicReports.type.stringType());
        TextColumnBuilder<String> colEnOrd = DynamicReports.col.column("En Ord.", "enOrd", DynamicReports.type.stringType());
        TextColumnBuilder<String> colDisp = DynamicReports.col.column("Disp.", "disp", DynamicReports.type.stringType());
        TextColumnBuilder<String> colCostoU = DynamicReports.col.column("Costo U.", "costoU", DynamicReports.type.stringType());
        TextColumnBuilder<String> colValorT = DynamicReports.col.column("Valor T.", "valorT", DynamicReports.type.stringType());
        TextColumnBuilder<String> colUltMov = DynamicReports.col.column("Últ. Mov.", "ultimoMov", DynamicReports.type.stringType());
        TextColumnBuilder<String> colDias = DynamicReports.col.column("Días S.M.", "dias", DynamicReports.type.stringType());
        TextColumnBuilder<String> colEstado = DynamicReports.col.column("Estado", "estado", DynamicReports.type.stringType());

        JasperReportBuilder report = DynamicReports.report()
                .setColumnTitleStyle(headerStyle)
                .setColumnStyle(columnStyle)
                .columns(colCodigo, colNombre, colUm, colStockAct, colStockMin, colComp, colEnOrd, colDisp, colCostoU, colValorT, colUltMov, colDias, colEstado)
                .title(DynamicReports.cmp.text("Reporte Analítico - Estado de Inventario (MRP)")
                        .setStyle(titleStyle))
                .pageFooter(DynamicReports.cmp.pageXofY())
                .setDataSource(new JRBeanCollectionDataSource(data));

        report.toPdf(out);
        return out.toByteArray();
    }

    public byte[] exportKpi(KpiReportDto kpis) throws DRException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        StyleBuilder titleStyle = DynamicReports.stl.style()
                .bold()
                .setFontSize(18)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setForegroundColor(new Color(10, 37, 64));

        StyleBuilder headerStyle = DynamicReports.stl.style()
                .bold()
                .setBackgroundColor(new Color(10, 37, 64))
                .setForegroundColor(Color.WHITE)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        StyleBuilder columnStyle = DynamicReports.stl.style()
                .setBorder(DynamicReports.stl.penThin())
                .setFontSize(10);

        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of("kpi", "Total Órdenes Sugeridas", "value", String.valueOf(kpis.getTotalOrdenesSugeridas())));
        data.add(Map.of("kpi", "Costo Total Órdenes Sugeridas", "value", kpis.getCostoTotalOrdenes() != null ? kpis.getCostoTotalOrdenes().toString() : "0"));
        data.add(Map.of("kpi", "Total Acciones Recomendadas", "value", String.valueOf(kpis.getTotalAcciones())));
        data.add(Map.of("kpi", "Acciones de Alta Prioridad", "value", String.valueOf(kpis.getAccionesUrgentes())));
        data.add(Map.of("kpi", "Total Excepciones en Sistema", "value", String.valueOf(kpis.getTotalExcepciones())));
        data.add(Map.of("kpi", "Excepciones Críticas", "value", String.valueOf(kpis.getExcepcionesCriticas())));
        data.add(Map.of("kpi", "Materiales con Stock Crítico (Cero)", "value", String.valueOf(kpis.getMaterialesCriticos())));
        data.add(Map.of("kpi", "Materiales con Stock Bajo (< Mínimo)", "value", String.valueOf(kpis.getMaterialesBajos())));
        data.add(Map.of("kpi", "Valor Total del Inventario ($)", "value", kpis.getValorTotalInventario() != null ? kpis.getValorTotalInventario().toString() : "0"));

        TextColumnBuilder<String> colKpi = DynamicReports.col.column("Indicador Key Performance (KPI)", "kpi", DynamicReports.type.stringType());
        TextColumnBuilder<String> colVal = DynamicReports.col.column("Valor", "value", DynamicReports.type.stringType());

        JasperReportBuilder report = DynamicReports.report()
                .setColumnTitleStyle(headerStyle)
                .setColumnStyle(columnStyle)
                .columns(colKpi, colVal)
                .title(DynamicReports.cmp.text("Reporte Gerencial - Dashboard MRP")
                        .setStyle(titleStyle))
                .pageFooter(DynamicReports.cmp.pageXofY())
                .setDataSource(new JRBeanCollectionDataSource(data));

        report.toPdf(out);
        return out.toByteArray();
    }

    private Map<String, Object> createInventoryMap(InventorySummaryDto i) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("codigo", i.getCodigo() != null ? i.getCodigo() : "");
        map.put("nombre", i.getNombre() != null ? i.getNombre() : "");
        map.put("um", i.getUnidadMedida() != null ? i.getUnidadMedida() : "");
        map.put("stockAct", i.getStockActual() != null ? i.getStockActual().toString() : "0");
        map.put("stockMin", i.getStockMinimo() != null ? i.getStockMinimo().toString() : "0");
        map.put("comp", i.getComprometido() != null ? i.getComprometido().toString() : "0");
        map.put("enOrd", i.getEnOrden() != null ? i.getEnOrden().toString() : "0");
        map.put("disp", i.getDisponibleNeto() != null ? i.getDisponibleNeto().toString() : "0");
        map.put("costoU", i.getCostoUnitario() != null ? i.getCostoUnitario().toString() : "0");
        map.put("valorT", i.getValorTotal() != null ? i.getValorTotal().toString() : "0");
        map.put("ultimoMov", i.getUltimoMovimiento() != null ? i.getUltimoMovimiento().format(DATE_FORMATTER) : "");
        map.put("dias", i.getDiasSinMovimiento() != null ? i.getDiasSinMovimiento().toString() : "0");
        map.put("estado", i.getEstado() != null ? i.getEstado() : "");
        return map;
    }
}
