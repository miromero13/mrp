package eccomerce.backend_eccomerce.reports.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import eccomerce.backend_eccomerce.reports.dto.ActionMessageDto;
import eccomerce.backend_eccomerce.reports.dto.ExceptionDto;
import eccomerce.backend_eccomerce.reports.dto.InventorySummaryDto;
import eccomerce.backend_eccomerce.reports.dto.OrdenSugeridaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public byte[] exportOrdenesSugeridasToExcel(List<OrdenSugeridaDto> ordenes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Órdenes Sugeridas");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            String[] headers = {"Código", "Nombre", "Tipo", "Requerido", "Disponible", "Ordenar",
                    "Fecha Necesidad", "Acción", "Costo Unitario", "Costo Total"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (OrdenSugeridaDto orden : ordenes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(orden.getCodigo());
                row.createCell(1).setCellValue(orden.getNombre());
                row.createCell(2).setCellValue(orden.getTipo());
                row.createCell(3).setCellValue(orden.getRequerido() != null ? orden.getRequerido().doubleValue() : 0);
                row.createCell(4).setCellValue(orden.getDisponible() != null ? orden.getDisponible().doubleValue() : 0);
                row.createCell(5).setCellValue(orden.getOrdenar() != null ? orden.getOrdenar().doubleValue() : 0);
                org.apache.poi.ss.usermodel.Cell fechaCell = row.createCell(6);
                if (orden.getFechaNecesidad() != null) {
                    fechaCell.setCellValue(orden.getFechaNecesidad().format(DATE_FORMATTER));
                }
                fechaCell.setCellStyle(dateStyle);
                row.createCell(7).setCellValue(orden.getAccion());
                row.createCell(8).setCellValue(orden.getCostoUnitario() != null ? orden.getCostoUnitario().doubleValue() : 0);
                row.createCell(9).setCellValue(orden.getCostoTotal() != null ? orden.getCostoTotal().doubleValue() : 0);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportAccionesToExcel(List<ActionMessageDto> acciones) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Mensajes de Acción");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle priorityHighStyle = createPriorityStyle(workbook, IndexedColors.RED);
            CellStyle priorityMediumStyle = createPriorityStyle(workbook, IndexedColors.ORANGE);

            String[] headers = {"Código", "Nombre", "Tipo Item", "Tipo Acción", "Fecha Anterior",
                    "Fecha Nueva", "Cantidad", "Razón", "Prioridad", "Fecha Actual"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (ActionMessageDto accion : acciones) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(accion.getCodigoItem());
                row.createCell(1).setCellValue(accion.getNombreItem());
                row.createCell(2).setCellValue(accion.getTipoItem());
                row.createCell(3).setCellValue(accion.getTipoAccion());
                row.createCell(4).setCellValue(accion.getFechaAnterior() != null ? accion.getFechaAnterior().format(DATE_FORMATTER) : "");
                row.createCell(5).setCellValue(accion.getFechaNueva() != null ? accion.getFechaNueva().format(DATE_FORMATTER) : "");
                row.createCell(6).setCellValue(accion.getCantidadNueva() != null ? accion.getCantidadNueva().doubleValue() : 0);
                row.createCell(7).setCellValue(accion.getRazon());
                org.apache.poi.ss.usermodel.Cell prioridadCell = row.createCell(8);
                prioridadCell.setCellValue(accion.getPrioridad());
                if ("HIGH".equals(accion.getPrioridad())) {
                    prioridadCell.setCellStyle(priorityHighStyle);
                } else if ("MEDIUM".equals(accion.getPrioridad())) {
                    prioridadCell.setCellStyle(priorityMediumStyle);
                }
                row.createCell(9).setCellValue(accion.getFechaActual() != null ? accion.getFechaActual().format(DATE_FORMATTER) : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportExcepcionesToExcel(List<ExceptionDto> excepciones) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Excepciones");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle criticalStyle = createPriorityStyle(workbook, IndexedColors.RED);
            CellStyle warningStyle = createPriorityStyle(workbook, IndexedColors.ORANGE);

            String[] headers = {"Código", "Nombre", "Tipo", "Tipo Excepción", "Cant. Actual",
                    "Cant. Mínima", "Cant. Afectada", "Severidad", "Estado", "Descripción"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (ExceptionDto exc : excepciones) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(exc.getCodigo());
                row.createCell(1).setCellValue(exc.getNombre());
                row.createCell(2).setCellValue(exc.getTipo());
                row.createCell(3).setCellValue(exc.getTipoExcepcion());
                row.createCell(4).setCellValue(exc.getCantidadActual() != null ? exc.getCantidadActual().doubleValue() : 0);
                row.createCell(5).setCellValue(exc.getCantidadMinima() != null ? exc.getCantidadMinima().doubleValue() : 0);
                row.createCell(6).setCellValue(exc.getCantidadAfectada() != null ? exc.getCantidadAfectada().doubleValue() : 0);
                org.apache.poi.ss.usermodel.Cell severityCell = row.createCell(7);
                severityCell.setCellValue(exc.getSeveridad());
                if ("CRITICAL".equals(exc.getSeveridad())) {
                    severityCell.setCellStyle(criticalStyle);
                } else if ("WARNING".equals(exc.getSeveridad())) {
                    severityCell.setCellStyle(warningStyle);
                }
                row.createCell(8).setCellValue(exc.getEstado());
                row.createCell(9).setCellValue(exc.getDescripcion());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportInventarioToExcel(List<InventorySummaryDto> inventario) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {"Código", "Nombre", "U. Medida", "Stock Actual", "Stock Mínimo",
                    "Comprometido", "En Orden", "Disponible Neto", "Costo Unitario", "Valor Total",
                    "Último Mov.", "Días Sin Mov.", "Estado"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (InventorySummaryDto inv : inventario) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(inv.getCodigo());
                row.createCell(1).setCellValue(inv.getNombre());
                row.createCell(2).setCellValue(inv.getUnidadMedida());
                row.createCell(3).setCellValue(inv.getStockActual() != null ? inv.getStockActual().doubleValue() : 0);
                row.createCell(4).setCellValue(inv.getStockMinimo() != null ? inv.getStockMinimo().doubleValue() : 0);
                row.createCell(5).setCellValue(inv.getComprometido() != null ? inv.getComprometido().doubleValue() : 0);
                row.createCell(6).setCellValue(inv.getEnOrden() != null ? inv.getEnOrden().doubleValue() : 0);
                row.createCell(7).setCellValue(inv.getDisponibleNeto() != null ? inv.getDisponibleNeto().doubleValue() : 0);
                row.createCell(8).setCellValue(inv.getCostoUnitario() != null ? inv.getCostoUnitario().doubleValue() : 0);
                row.createCell(9).setCellValue(inv.getValorTotal() != null ? inv.getValorTotal().doubleValue() : 0);
                row.createCell(10).setCellValue(inv.getUltimoMovimiento() != null ? inv.getUltimoMovimiento().format(DATE_FORMATTER) : "");
                row.createCell(11).setCellValue(inv.getDiasSinMovimiento() != null ? inv.getDiasSinMovimiento() : 0);
                row.createCell(12).setCellValue(inv.getEstado());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public String generateHtmlOrdenesSugeridas(List<OrdenSugeridaDto> ordenes) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Ordenes Sugeridas</title>");
        html.append("<style>body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("h1 { color: #333; }table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #4CAF50; color: white; }");
        html.append("tr:nth-child(even) { background-color: #f2f2f2; }");
        html.append(".action-buy { color: #2196F3; font-weight: bold; }");
        html.append(".action-make { color: #FF9800; font-weight: bold; }</style>");
        html.append("</head><body><h1>Ordenes Sugeridas - MRP</h1>");
        html.append("<p>Fecha de generacion: ").append(LocalDate.now().format(DATE_FORMATTER)).append("</p>");
        html.append("<table><tr><th>Codigo</th><th>Nombre</th><th>Tipo</th><th>Requerido</th>");
        html.append("<th>Disponible</th><th>Ordenar</th><th>Fecha Necesidad</th><th>Accion</th><th>Costo Total</th></tr>");

        for (OrdenSugeridaDto orden : ordenes) {
            String actionClass = "COMPRAR".equals(orden.getAccion()) ? "action-buy" : "action-make";
            html.append("<tr>");
            html.append("<td>").append(orden.getCodigo()).append("</td>");
            html.append("<td>").append(orden.getNombre()).append("</td>");
            html.append("<td>").append(orden.getTipo()).append("</td>");
            html.append("<td>").append(orden.getRequerido() != null ? orden.getRequerido() : 0).append("</td>");
            html.append("<td>").append(orden.getDisponible() != null ? orden.getDisponible() : 0).append("</td>");
            html.append("<td>").append(orden.getOrdenar() != null ? orden.getOrdenar() : 0).append("</td>");
            html.append("<td>").append(orden.getFechaNecesidad() != null ? orden.getFechaNecesidad().format(DATE_FORMATTER) : "-").append("</td>");
            html.append("<td class=\"").append(actionClass).append("\">").append(orden.getAccion()).append("</td>");
            html.append("<td>").append(orden.getCostoTotal() != null ? orden.getCostoTotal() : 0).append("</td>");
            html.append("</tr>");
        }

        html.append("</table></body></html>");

        return html.toString();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createPriorityStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}