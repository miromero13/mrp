package eccomerce.backend_eccomerce.reports.controller;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.SecurityUtils;
import eccomerce.backend_eccomerce.reports.dto.gerencial.KpiReportDto;
import eccomerce.backend_eccomerce.reports.service.MrpCalculationService;
import eccomerce.backend_eccomerce.reports.service.export.CsvExportService;
import eccomerce.backend_eccomerce.reports.service.export.ExcelExportService;
import eccomerce.backend_eccomerce.reports.service.export.PdfExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/reports/gerenciales")
@RequiredArgsConstructor
@Slf4j
public class GerencialReportController {

    private final MrpCalculationService mrpCalculationService;
    private final SecurityUtils securityUtils;
    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;
    private final CsvExportService csvExportService;

    @RequirePermission(PermissionConstants.VER_REPORTES_GERENCIALES)
    @GetMapping("/dashboard")
    public ResponseEntity<KpiReportDto> getDashboardKpis() {
        UUID enterpriseId = securityUtils.getCurrentEnterpriseId().orElse(null);
        log.info("Fetching management dashboard KPIs for enterprise: {}", enterpriseId);
        KpiReportDto kpis = mrpCalculationService.getDashboardSummary(enterpriseId);
        return ResponseEntity.ok(kpis);
    }

    @RequirePermission(PermissionConstants.EXPORTAR_REPORTES)
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportDashboardKpis(@RequestParam(name = "format", defaultValue = "pdf") String format) throws Exception {
        UUID enterpriseId = securityUtils.getCurrentEnterpriseId().orElse(null);
        log.info("Exporting management dashboard KPIs in {} format for enterprise: {}", format, enterpriseId);
        KpiReportDto kpis = mrpCalculationService.getDashboardSummary(enterpriseId);

        byte[] content;
        String filename;
        MediaType mediaType;

        switch (format.toLowerCase()) {
            case "excel":
            case "xlsx":
                content = excelExportService.exportKpis(kpis);
                filename = "kpi-dashboard.xlsx";
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            case "csv":
                content = csvExportService.exportKpis(kpis);
                filename = "kpi-dashboard.csv";
                mediaType = new MediaType("text", "csv");
                break;
            case "pdf":
            default:
                content = pdfExportService.exportKpi(kpis);
                filename = "kpi-dashboard.pdf";
                mediaType = MediaType.APPLICATION_PDF;
                break;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(content);
    }
}
