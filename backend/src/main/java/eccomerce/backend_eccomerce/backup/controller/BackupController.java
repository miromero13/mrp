package eccomerce.backend_eccomerce.backup.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.quartz.SchedulerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import eccomerce.backend_eccomerce.backup.dto.BackupSessionDto;
import eccomerce.backend_eccomerce.backup.dto.ScheduleBackupDto;
import eccomerce.backend_eccomerce.backup.entity.BackupEntity;
import eccomerce.backend_eccomerce.backup.enums.BackupType;
import eccomerce.backend_eccomerce.backup.repository.BackupRepository;
import eccomerce.backend_eccomerce.backup.scheduler.BackupSchedulerConfig;
import eccomerce.backend_eccomerce.backup.service.BackupService;
import eccomerce.backend_eccomerce.backup.service.BackupStorageService;
import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import eccomerce.backend_eccomerce.common.constants.PermissionConstants;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/backups")
public class BackupController {

    @Autowired
    private BackupService backupService;
    @Autowired
    private BackupStorageService storageService;
    @Autowired
    private BackupSchedulerConfig schedulerConfig;
    @Autowired
    private BackupRepository backupRepository;

    @RequirePermission(PermissionConstants.CREAR_BACKUP)
    @Operation(summary = "Generar backup manual")
    @PostMapping("/manual")
    public ResponseMessage<Void> backupManual(@RequestBody BackupSessionDto dto) {
        UUID empresaId = obtenerEmpresaId(dto);
        backupService.generarBackup(empresaId, BackupType.MANUAL);
        return ResponseMessage.ok("Backup iniciado correctamente", 202);
    }

    @RequirePermission(PermissionConstants.CREAR_BACKUP)
    @Operation(summary = "Generar backup programado")
    @PostMapping("/programar")
    public ResponseMessage<Void> programar(@RequestBody ScheduleBackupDto backupDto,
            BackupSessionDto dto) throws SchedulerException {
        UUID empresaId = obtenerEmpresaId(dto);
        // req.getCronExpression() ej: "0 0 2 * * ?" = todos los días a las 2am
        schedulerConfig.programarBackup(empresaId, backupDto.getCronExpression());
        return ResponseMessage.ok("Backup programado correctamente", 202);
    }

    @RequirePermission(PermissionConstants.CANCELAR_BACKUP)
    @Operation(summary = "Cancelar backup programado")
    @DeleteMapping("/programar")
    public ResponseMessage<Void> cancelarProgramado(BackupSessionDto dto) throws SchedulerException {
        UUID empresaId = obtenerEmpresaId(dto);
        schedulerConfig.cancelarBackupProgramado(empresaId);
        return ResponseMessage.ok("Backup programado cancelado correctamente", 202);
    }

    @RequirePermission(PermissionConstants.LISTAR_BACKUPS)
    @Operation(summary = "Obtener historial de backups")
    @GetMapping
    public ResponseMessage<List<BackupEntity>> historial(BackupSessionDto dto) {
        UUID empresaId = obtenerEmpresaId(dto);
        return backupService.getAllBackups(empresaId);
    }

    @GetMapping("/descargar/{backupId}")
    @RequirePermission(PermissionConstants.DESCARGAR_BACKUP)
    @Operation(summary = "Descargar backup")
    public ResponseEntity<Resource> descargar(@PathVariable Long backupId,
            BackupSessionDto dto) throws IOException {
        UUID empresaId = obtenerEmpresaId(dto);
        BackupEntity backup = backupRepository.findByIdAndEnterpriseId(backupId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Resource resource = storageService.cargar(backup.getFilePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + backup.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * Obtener el Id de la empresa de la sesión del usuario administrador
     * autenticado
     * 
     * @param dto tipo UserSessionDto
     * @return UUID
     */
    private UUID obtenerEmpresaId(BackupSessionDto dto) {
        return dto.getEnterpriseId();
    }

}
