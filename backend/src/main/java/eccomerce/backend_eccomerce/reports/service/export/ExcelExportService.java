package eccomerce.backend_eccomerce.reports.service.export;

import eccomerce.backend_eccomerce.reports.dto.ActionMessageDto;
import eccomerce.backend_eccomerce.reports.dto.ExceptionDto;
import eccomerce.backend_eccomerce.reports.dto.InventorySummaryDto;
import eccomerce.backend_eccomerce.reports.dto.OrdenSugeridaDto;
import eccomerce.backend_eccomerce.reports.dto.gerencial.KpiReportDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public byte[] exportOrdenesSugeridas(List<OrdenSugeridaDto> ordenes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Órdenes Sugeridas");

            CellStyle headerStyle = createPremiumHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            String[] headers = {"Código", "Nombre", "Tipo", "Requerido", "Disponible", "Ordenar",
                    "Fecha Necesidad", "Acción", "Costo Unitario", "Costo Total"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (OrdenSugeridaDto orden : ordenes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(orden.getCodigo());
                row.createCell(1).setCellValue(orden.getNombre());
                row.createCell(2).setCellValue(orden.getTipo());
                
                Cell reqCell = row.createCell(3);
                reqCell.setCellValue(orden.getRequerido() != null ? orden.getRequerido().doubleValue() : 0);
                reqCell.setCellStyle(numberStyle);

                Cell dispCell = row.createCell(4);
                dispCell.setCellValue(orden.getDisponible() != null ? orden.getDisponible().doubleValue() : 0);
                dispCell.setCellStyle(numberStyle);

                Cell ordCell = row.createCell(5);
                ordCell.setCellValue(orden.getOrdenar() != null ? orden.getOrdenar().doubleValue() : 0);
                ordCell.setCellStyle(numberStyle);

                Cell fechaCell = row.createCell(6);
                if (orden.getFechaNecesidad() != null) {
                    fechaCell.setCellValue(orden.getFechaNecesidad().format(DATE_FORMATTER));
                }
                fechaCell.setCellStyle(dateStyle);

                row.createCell(7).setCellValue(orden.getAccion());

                Cell cuCell = row.createCell(8);
                cuCell.setCellValue(orden.getCostoUnitario() != null ? orden.getCostoUnitario().doubleValue() : 0);
                cuCell.setCellStyle(numberStyle);

                Cell ctCell = row.createCell(9);
                ctCell.setCellValue(orden.getCostoTotal() != null ? orden.getCostoTotal().doubleValue() : 0);
                ctCell.setCellStyle(numberStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportAcciones(List<ActionMessageDto> acciones) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Mensajes de Acción");

            CellStyle headerStyle = createPremiumHeaderStyle(workbook);
            CellStyle priorityHighStyle = createPriorityStyle(workbook, IndexedColors.RED);
            CellStyle priorityMediumStyle = createPriorityStyle(workbook, IndexedColors.ORANGE);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            String[] headers = {"Código", "Nombre", "Tipo Item", "Tipo Acción", "Fecha Anterior",
                    "Fecha Nueva", "Cantidad", "Razón", "Prioridad", "Fecha Actual"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
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
                
                Cell prevCell = row.createCell(4);
                prevCell.setCellValue(accion.getFechaAnterior() != null ? accion.getFechaAnterior().format(DATE_FORMATTER) : "");
                prevCell.setCellStyle(dateStyle);

                Cell newCell = row.createCell(5);
                newCell.setCellValue(accion.getFechaNueva() != null ? accion.getFechaNueva().format(DATE_FORMATTER) : "");
                newCell.setCellStyle(dateStyle);

                Cell qtyCell = row.createCell(6);
                qtyCell.setCellValue(accion.getCantidadNueva() != null ? accion.getCantidadNueva().doubleValue() : 0);
                qtyCell.setCellStyle(numberStyle);

                row.createCell(7).setCellValue(accion.getRazon());
                
                Cell prioridadCell = row.createCell(8);
                prioridadCell.setCellValue(accion.getPrioridad());
                if ("HIGH".equals(accion.getPrioridad())) {
                    prioridadCell.setCellStyle(priorityHighStyle);
                } else if ("MEDIUM".equals(accion.getPrioridad())) {
                    prioridadCell.setCellStyle(priorityMediumStyle);
                } else {
                    prioridadCell.setCellStyle(dateStyle);
                }

                Cell actCell = row.createCell(9);
                actCell.setCellValue(accion.getFechaActual() != null ? accion.getFechaActual().format(DATE_FORMATTER) : "");
                actCell.setCellStyle(dateStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportExcepciones(List<ExceptionDto> excepciones) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Excepciones");

            CellStyle headerStyle = createPremiumHeaderStyle(workbook);
            CellStyle criticalStyle = createPriorityStyle(workbook, IndexedColors.RED);
            CellStyle warningStyle = createPriorityStyle(workbook, IndexedColors.ORANGE);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle centerStyle = createDateStyle(workbook);

            String[] headers = {"Código", "Nombre", "Tipo", "Tipo Excepción", "Cant. Actual",
                    "Cant. Mínima", "Cant. Afectada", "Severidad", "Estado", "Descripción"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
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
                
                Cell actCell = row.createCell(4);
                actCell.setCellValue(exc.getCantidadActual() != null ? exc.getCantidadActual().doubleValue() : 0);
                actCell.setCellStyle(numberStyle);

                Cell minCell = row.createCell(5);
                minCell.setCellValue(exc.getCantidadMinima() != null ? exc.getCantidadMinima().doubleValue() : 0);
                minCell.setCellStyle(numberStyle);

                Cell afCell = row.createCell(6);
                afCell.setCellValue(exc.getCantidadAfectada() != null ? exc.getCantidadAfectada().doubleValue() : 0);
                afCell.setCellStyle(numberStyle);

                Cell severityCell = row.createCell(7);
                severityCell.setCellValue(exc.getSeveridad());
                if ("CRITICAL".equals(exc.getSeveridad())) {
                    severityCell.setCellStyle(criticalStyle);
                } else if ("WARNING".equals(exc.getSeveridad())) {
                    severityCell.setCellStyle(warningStyle);
                } else {
                    severityCell.setCellStyle(centerStyle);
                }

                Cell stateCell = row.createCell(8);
                stateCell.setCellValue(exc.getEstado());
                stateCell.setCellStyle(centerStyle);

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

    public byte[] exportInventario(List<InventorySummaryDto> inventario) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            CellStyle headerStyle = createPremiumHeaderStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            String[] headers = {"Código", "Nombre", "U. Medida", "Stock Actual", "Stock Mínimo",
                    "Comprometido", "En Orden", "Disponible Neto", "Costo Unitario", "Valor Total",
                    "Último Mov.", "Días Sin Mov.", "Estado"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (InventorySummaryDto inv : inventario) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(inv.getCodigo());
                row.createCell(1).setCellValue(inv.getNombre());
                
                Cell umCell = row.createCell(2);
                umCell.setCellValue(inv.getUnidadMedida());
                umCell.setCellStyle(dateStyle);

                Cell actCell = row.createCell(3);
                actCell.setCellValue(inv.getStockActual() != null ? inv.getStockActual().doubleValue() : 0);
                actCell.setCellStyle(numberStyle);

                Cell minCell = row.createCell(4);
                minCell.setCellValue(inv.getStockMinimo() != null ? inv.getStockMinimo().doubleValue() : 0);
                minCell.setCellStyle(numberStyle);

                Cell compCell = row.createCell(5);
                compCell.setCellValue(inv.getComprometido() != null ? inv.getComprometido().doubleValue() : 0);
                compCell.setCellStyle(numberStyle);

                Cell ordCell = row.createCell(6);
                ordCell.setCellValue(inv.getEnOrden() != null ? inv.getEnOrden().doubleValue() : 0);
                ordCell.setCellStyle(numberStyle);

                Cell dispCell = row.createCell(7);
                dispCell.setCellValue(inv.getDisponibleNeto() != null ? inv.getDisponibleNeto().doubleValue() : 0);
                dispCell.setCellStyle(numberStyle);

                Cell cuCell = row.createCell(8);
                cuCell.setCellValue(inv.getCostoUnitario() != null ? inv.getCostoUnitario().doubleValue() : 0);
                cuCell.setCellStyle(numberStyle);

                Cell valCell = row.createCell(9);
                valCell.setCellValue(inv.getValorTotal() != null ? inv.getValorTotal().doubleValue() : 0);
                valCell.setCellStyle(numberStyle);

                Cell fechaCell = row.createCell(10);
                fechaCell.setCellValue(inv.getUltimoMovimiento() != null ? inv.getUltimoMovimiento().format(DATE_FORMATTER) : "");
                fechaCell.setCellStyle(dateStyle);

                Cell diasCell = row.createCell(11);
                diasCell.setCellValue(inv.getDiasSinMovimiento() != null ? inv.getDiasSinMovimiento() : 0);
                diasCell.setCellStyle(numberStyle);

                Cell stateCell = row.createCell(12);
                stateCell.setCellValue(inv.getEstado());
                stateCell.setCellStyle(dateStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportKpis(KpiReportDto kpis) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Dashboard Gerencial");

            CellStyle headerStyle = createPremiumHeaderStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle labelStyle = createLabelStyle(workbook);

            String[] headers = {"Indicador Key Performance (KPI)", "Valor"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            Object[][] data = {
                    {"Total Órdenes Sugeridas", kpis.getTotalOrdenesSugeridas()},
                    {"Costo Total Órdenes Sugeridas", kpis.getCostoTotalOrdenes() != null ? kpis.getCostoTotalOrdenes().doubleValue() : 0},
                    {"Total Acciones Recomendadas", kpis.getTotalAcciones()},
                    {"Acciones de Alta Prioridad", kpis.getAccionesUrgentes()},
                    {"Total Excepciones en Sistema", kpis.getTotalExcepciones()},
                    {"Excepciones Críticas", kpis.getExcepcionesCriticas()},
                    {"Materiales con Stock Crítico (Cero)", kpis.getMaterialesCriticos()},
                    {"Materiales con Stock Bajo (< Mínimo)", kpis.getMaterialesBajos()},
                    {"Valor Total del Inventario ($)", kpis.getValorTotalInventario() != null ? kpis.getValorTotalInventario().doubleValue() : 0}
            };

            int rowNum = 1;
            for (Object[] item : data) {
                Row row = sheet.createRow(rowNum++);
                
                Cell labelCell = row.createCell(0);
                labelCell.setCellValue((String) item[0]);
                labelCell.setCellStyle(labelStyle);

                Cell valCell = row.createCell(1);
                if (item[1] instanceof Integer) {
                    valCell.setCellValue((Integer) item[1]);
                } else if (item[1] instanceof Long) {
                    valCell.setCellValue((Long) item[1]);
                } else {
                    valCell.setCellValue((Double) item[1]);
                }
                valCell.setCellStyle(numberStyle);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle createPremiumHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // Premium Dark Blue
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Arial");
        style.setFont(font);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createPriorityStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
