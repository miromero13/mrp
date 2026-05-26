package eccomerce.backend_eccomerce.reports.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.reports.dto.ActionMessageDto;
import eccomerce.backend_eccomerce.reports.dto.ExceptionDto;
import eccomerce.backend_eccomerce.reports.dto.InventorySummaryDto;
import eccomerce.backend_eccomerce.reports.dto.OrdenSugeridaDto;
import eccomerce.backend_eccomerce.reports.service.ExportService;
import eccomerce.backend_eccomerce.reports.service.MrpCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final MrpCalculationService mrpCalculationService;
    private final ExportService exportService;

    @RequirePermission(PermissionConstants.LISTAR_REPORTES)
    @GetMapping("/ordenes-sugeridas")
    public ResponseEntity<List<OrdenSugeridaDto>> getOrdenesSugeridas() {
        log.info("Generando reporte de órdenes sugeridas");
        List<OrdenSugeridaDto> ordenes = mrpCalculationService.generateOrdenesSugeridas();
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/ordenes-sugeridas/excel")
    public ResponseEntity<byte[]> exportOrdenesSugeridasExcel() throws IOException {
        log.info("Exportando órdenes sugeridas a Excel");
        List<OrdenSugeridaDto> ordenes = mrpCalculationService.generateOrdenesSugeridas();
        byte[] excel = exportService.exportOrdenesSugeridasToExcel(ordenes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ordenes-sugeridas.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @GetMapping("/ordenes-sugeridas/html")
    public ResponseEntity<String> exportOrdenesSugeridasHtml() {
        log.info("Generando órdenes sugeridas en HTML");
        List<OrdenSugeridaDto> ordenes = mrpCalculationService.generateOrdenesSugeridas();
        String html = exportService.generateHtmlOrdenesSugeridas(ordenes);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping("/acciones")
    public ResponseEntity<List<ActionMessageDto>> getAcciones() {
        log.info("Generando reporte de acciones");
        List<ActionMessageDto> acciones = mrpCalculationService.generateActionMessages();
        return ResponseEntity.ok(acciones);
    }

    @GetMapping("/acciones/excel")
    public ResponseEntity<byte[]> exportAccionesExcel() throws IOException {
        log.info("Exportando acciones a Excel");
        List<ActionMessageDto> acciones = mrpCalculationService.generateActionMessages();
        byte[] excel = exportService.exportAccionesToExcel(acciones);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=acciones.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @RequirePermission(PermissionConstants.LISTAR_REPORTES)
    @GetMapping("/excepciones")
    public ResponseEntity<List<ExceptionDto>> getExcepciones() {
        log.info("Generando reporte de excepciones");
        List<ExceptionDto> excepciones = mrpCalculationService.generateExceptions();
        return ResponseEntity.ok(excepciones);
    }

    @GetMapping("/excepciones/excel")
    public ResponseEntity<byte[]> exportExcepcionesExcel() throws IOException {
        log.info("Exportando excepciones a Excel");
        List<ExceptionDto> excepciones = mrpCalculationService.generateExceptions();
        byte[] excel = exportService.exportExcepcionesToExcel(excepciones);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=excepciones.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @RequirePermission(PermissionConstants.LISTAR_REPORTES)
    @GetMapping("/inventario")
    public ResponseEntity<List<InventorySummaryDto>> getInventario() {
        log.info("Generando reporte de inventario");
        List<InventorySummaryDto> inventario = mrpCalculationService.generateInventorySummary();
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/inventario/excel")
    public ResponseEntity<byte[]> exportInventarioExcel() throws IOException {
        log.info("Exportando inventario a Excel");
        List<InventorySummaryDto> inventario = mrpCalculationService.generateInventorySummary();
        byte[] excel = exportService.exportInventarioToExcel(inventario);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventario.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        log.info("Generando dashboard MRP");
        Map<String, Object> summary = mrpCalculationService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}