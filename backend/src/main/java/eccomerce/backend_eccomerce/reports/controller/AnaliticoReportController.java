package eccomerce.backend_eccomerce.reports.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.SecurityUtils;
import eccomerce.backend_eccomerce.reports.dto.ActionMessageDto;
import eccomerce.backend_eccomerce.reports.dto.ExceptionDto;
import eccomerce.backend_eccomerce.reports.dto.InventorySummaryDto;
import eccomerce.backend_eccomerce.reports.dto.OrdenSugeridaDto;
import eccomerce.backend_eccomerce.reports.service.MrpCalculationService;
import eccomerce.backend_eccomerce.reports.service.export.CsvExportService;
import eccomerce.backend_eccomerce.reports.service.export.ExcelExportService;
import eccomerce.backend_eccomerce.reports.service.export.PdfExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reports/analiticos")
@RequiredArgsConstructor
@Slf4j
public class AnaliticoReportController {

    private final MrpCalculationService mrpCalculationService;
    private final SecurityUtils securityUtils;
    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;
    private final CsvExportService csvExportService;

    @RequirePermission(PermissionConstants.VER_REPORTES_ANALITICOS)
    @GetMapping("/ordenes-sugeridas")
    public ResponseEntity<List<OrdenSugeridaDto>> getOrdenesSugeridas(
            @RequestParam(name = "materialCode", required = false) String materialCode,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        UUID enterpriseId = securityUtils.getCurrentEnterpriseId().orElse(null);
        List<OrdenSugeridaDto> list = mrpCalculationService.generateOrdenesSugeridas(enterpriseId);

        // Apply filters
        if (materialCode != null && !materialCode.isBlank()) {
            list = list.stream().filter(o -> o.getCodigo() != null && o.getCodigo().equalsIgnoreCase(materialCode.trim())).collect(Collectors.toList());
        }
        if (startDate != null) {
            list = list.stream().filter(o -> o.getFechaNecesidad() != null && !o.getFechaNecesidad().isBefore(startDate)).collect(Collectors.toList());
        }
        if (endDate != null) {
            list = list.stream().filter(o -> o.getFechaNecesidad() != null && !o.getFechaNecesidad().isAfter(endDate)).collect(Collectors.toList());
        }

        return ResponseEntity.ok(list);
    }

    @RequirePermission(PermissionConstants.EXPORTAR_REPORTES)
    @GetMapping("/ordenes-sugeridas/export")
    public ResponseEntity<byte[]> exportOrdenesSugeridas(
            @RequestParam(name = "format", defaultValue = "pdf") String format,
            @RequestParam(name = "materialCode", required = false) String materialCode,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws Exception {

        List<OrdenSugeridaDto> list = getOrdenesSugeridas(materialCode, startDate, endDate).getBody();
        byte[] content;
        String filename;
        MediaType mediaType;

        switch (format.toLowerCase()) {
            case "excel":
            case "xlsx":
                content = excelExportService.exportOrdenesSugeridas(list);
                filename = "ordenes-sugeridas.xlsx";
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            case "csv":
                content = csvExportService.exportOrdenesSugeridas(list);
                filename = "ordenes-sugeridas.csv";
                mediaType = new MediaType("text", "csv");
                break;
            case "pdf":
            default:
                content = pdfExportService.exportOrdenesSugeridas(list);
                filename = "ordenes-sugeridas.pdf";
                mediaType = MediaType.APPLICATION_PDF;
                break;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(content);
    }

    @RequirePermission(PermissionConstants.VER_REPORTES_ANALITICOS)
    @GetMapping("/acciones")
    public ResponseEntity<List<ActionMessageDto>> getAcciones(
            @RequestParam(name = "materialCode", required = false) String materialCode,
            @RequestParam(name = "priority", required = false) String priority,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        UUID enterpriseId = securityUtils.getCurrentEnterpriseId().orElse(null);
        List<ActionMessageDto> list = mrpCalculationService.generateActionMessages(enterpriseId);

        // Apply filters
        if (materialCode != null && !materialCode.isBlank()) {
            list = list.stream().filter(a -> a.getCodigoItem() != null && a.getCodigoItem().equalsIgnoreCase(materialCode.trim())).collect(Collectors.toList());
        }
        if (priority != null && !priority.isBlank()) {
            list = list.stream().filter(a -> a.getPrioridad() != null && a.getPrioridad().equalsIgnoreCase(priority.trim())).collect(Collectors.toList());
        }
        if (startDate != null) {
            list = list.stream().filter(a -> a.getFechaActual() != null && !a.getFechaActual().isBefore(startDate)).collect(Collectors.toList());
        }
        if (endDate != null) {
            list = list.stream().filter(a -> a.getFechaActual() != null && !a.getFechaActual().isAfter(endDate)).collect(Collectors.toList());
        }

        return ResponseEntity.ok(list);
    }

    @RequirePermission(PermissionConstants.EXPORTAR_REPORTES)
    @GetMapping("/acciones/export")
    public ResponseEntity<byte[]> exportAcciones(
            @RequestParam(name = "format", defaultValue = "pdf") String format,
            @RequestParam(name = "materialCode", required = false) String materialCode,
            @RequestParam(name = "priority", required = false) String priority,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws Exception {

        List<ActionMessageDto> list = getAcciones(materialCode, priority, startDate, endDate).getBody();
        byte[] content;
        String filename;
        MediaType mediaType;

        switch (format.toLowerCase()) {
            case "excel":
            case "xlsx":
                content = excelExportService.exportAcciones(list);
                filename = "acciones.xlsx";
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            case "csv":
                content = csvExportService.exportAcciones(list);
                filename = "acciones.csv";
                mediaType = new MediaType("text", "csv");
                break;
            case "pdf":
            default:
                content = pdfExportService.exportAcciones(list);
                filename = "acciones.pdf";
                mediaType = MediaType.APPLICATION_PDF;
                break;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(content);
    }

    @RequirePermission(PermissionConstants.VER_REPORTES_ANALITICOS)
    @GetMapping("/excepciones")
    public ResponseEntity<List<ExceptionDto>> getExcepciones(
            @RequestParam(name = "materialCode", required = false) String materialCode,
            @RequestParam(name = "severity", required = false) String severity,
            @RequestParam(name = "status", required = false) String status) {

        UUID enterpriseId = securityUtils.getCurrentEnterpriseId().orElse(null);
        List<ExceptionDto> list = mrpCalculationService.generateExceptions(enterpriseId);

        // Apply filters
        if (materialCode != null && !materialCode.isBlank()) {
            list = list.stream().filter(e -> e.getCodigo() != null && e.getCodigo().equalsIgnoreCase(materialCode.trim())).collect(Collectors.toList());
        }
        if (severity != null && !severity.isBlank()) {
            list = list.stream().filter(e -> e.getSeveridad() != null && e.getSeveridad().equalsIgnoreCase(severity.trim())).collect(Collectors.toList());
        }
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(e -> e.getEstado() != null && e.getEstado().equalsIgnoreCase(status.trim())).collect(Collectors.toList());
        }

        return ResponseEntity.ok(list);
    }

    @RequirePermission(PermissionConstants.EXPORTAR_REPORTES)
    @GetMapping("/excepciones/export")
    public ResponseEntity<byte[]> exportExcepciones(
            @RequestParam(name = "format", defaultValue = "pdf") String format,
            @RequestParam(name = "materialCode", required = false) String materialCode,
            @RequestParam(name = "severity", required = false) String severity,
            @RequestParam(name = "status", required = false) String status) throws Exception {

        List<ExceptionDto> list = getExcepciones(materialCode, severity, status).getBody();
        byte[] content;
        String filename;
        MediaType mediaType;

        switch (format.toLowerCase()) {
            case "excel":
            case "xlsx":
                content = excelExportService.exportExcepciones(list);
                filename = "excepciones.xlsx";
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            case "csv":
                content = csvExportService.exportExcepciones(list);
                filename = "excepciones.csv";
                mediaType = new MediaType("text", "csv");
                break;
            case "pdf":
            default:
                content = pdfExportService.exportExcepciones(list);
                filename = "excepciones.pdf";
                mediaType = MediaType.APPLICATION_PDF;
                break;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(content);
    }

    @RequirePermission(PermissionConstants.VER_REPORTES_ANALITICOS)
    @GetMapping("/inventario")
    public ResponseEntity<List<InventorySummaryDto>> getInventario(
            @RequestParam(name = "materialCode", required = false) String materialCode,
            @RequestParam(name = "status", required = false) String status) {

        UUID enterpriseId = securityUtils.getCurrentEnterpriseId().orElse(null);
        List<InventorySummaryDto> list = mrpCalculationService.generateInventorySummary(enterpriseId);

        // Apply filters
        if (materialCode != null && !materialCode.isBlank()) {
            list = list.stream().filter(i -> i.getCodigo() != null && i.getCodigo().equalsIgnoreCase(materialCode.trim())).collect(Collectors.toList());
        }
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(i -> i.getEstado() != null && i.getEstado().equalsIgnoreCase(status.trim())).collect(Collectors.toList());
        }

        return ResponseEntity.ok(list);
    }

    @RequirePermission(PermissionConstants.EXPORTAR_REPORTES)
    @GetMapping("/inventario/export")
    public ResponseEntity<byte[]> exportInventario(
            @RequestParam(name = "format", defaultValue = "pdf") String format,
            @RequestParam(name = "materialCode", required = false) String materialCode,
            @RequestParam(name = "status", required = false) String status) throws Exception {

        List<InventorySummaryDto> list = getInventario(materialCode, status).getBody();
        byte[] content;
        String filename;
        MediaType mediaType;

        switch (format.toLowerCase()) {
            case "excel":
            case "xlsx":
                content = excelExportService.exportInventario(list);
                filename = "inventario.xlsx";
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            case "csv":
                content = csvExportService.exportInventario(list);
                filename = "inventario.csv";
                mediaType = new MediaType("text", "csv");
                break;
            case "pdf":
            default:
                content = pdfExportService.exportInventario(list);
                filename = "inventario.pdf";
                mediaType = MediaType.APPLICATION_PDF;
                break;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(content);
    }
}
