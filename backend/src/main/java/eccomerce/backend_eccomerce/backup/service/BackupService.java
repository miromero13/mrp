package eccomerce.backend_eccomerce.backup.service;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eccomerce.backend_eccomerce.backup.entity.BackupEntity;
import eccomerce.backend_eccomerce.backup.enums.BackupState;
import eccomerce.backend_eccomerce.backup.enums.BackupType;
import eccomerce.backend_eccomerce.backup.repository.BackupRepository;
import eccomerce.backend_eccomerce.common.utils.ResponseMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BackupService {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private BackupRepository backupRepository;
    @Autowired
    private BackupStorageService storageService;

    // Tablas que participan en el backup (con su columna tenant)
    private static final Map<String, String> TABLAS_TENANT = Map.of(
            "work_shifts", "enterprise_id",
            "users", "enterprise_id",
            "products", "enterprise_id",
            "materials", "enterprise_id",
            "enterprise_machines", "enterprise_id",
            "enterprise_indirect_costs", "enterprise_id");

    @Async  // BackupService
    public CompletableFuture<BackupEntity> generarBackup(UUID empresaId, BackupType tipo) {
        log.info(">>> generarBackup llamado para empresa: {}", empresaId);

        return CompletableFuture.supplyAsync(() -> {
            BackupEntity backup = new BackupEntity();
            backup.setEnterpriseId(empresaId);
            backup.setType(tipo);
            backup.setState(BackupState.EN_PROGRESO);
            backup.setStartDate(LocalDateTime.now());
            backupRepository.save(backup);
            
            try {
                // 1. Exportar datos filtrados por empresa
                Path archivoSql = exportarDatosEmpresa(empresaId);

                // 2. Comprimir
                Path archivoZip = comprimirArchivo(archivoSql);

                // Obtener tamaño ANTES de mover
                long archivoSize = Files.size(archivoZip);

                // 3. Guardar en storage (local o nube)
                String rutaFinal = storageService.guardar(archivoZip, empresaId);

                // 4. Actualizar registro
                backup.setFileName(archivoZip.getFileName().toString());
                backup.setFilePath(rutaFinal);
                backup.setSizeBytes(archivoSize);
                backup.setState(BackupState.COMPLETADO);
                backup.setMessage("Se ha generado el backup correctamente");
       
                // Limpiar temporales
                Files.deleteIfExists(archivoSql);
                Files.deleteIfExists(archivoZip);
                log.info(">>> Backup completado para empresa: {}", empresaId);

            } catch (Exception e) {
                log.error("Error generando backup para empresa {}", empresaId, e);
                backup.setState(BackupState.ERROR);
                backup.setMessage(e.getMessage());
            } finally {
                backup.setEndDate(LocalDateTime.now());
                backupRepository.save(backup);
            }
            return backup;
        });
    }

    public ResponseMessage<List<BackupEntity>> getAllBackups(UUID empresaId) {
        try {
            System.out.println(">>> getAllBackups llamado para empresa: " + empresaId);
            List<BackupEntity> backups = backupRepository.findByEnterpriseIdOrderByStartDateDesc(empresaId);
            return ResponseMessage.success(backups, "Backups obtenidas correctamente", backups.size());
        } catch (RuntimeException ex) {
            log.error("Error al obtener los backup de la empresa {}", empresaId, ex);
            return ResponseMessage.error("No se pudieron obtener los backups", ex.getMessage(), 400);
        } catch (Exception ex) {
            log.error("Error inesperadp al obtener los backup de la empresa {}", empresaId, ex);
            return ResponseMessage.error("No se pudieron obtener los backups", ex.getMessage(), 500);
        }
    }

    private Path exportarDatosEmpresa(UUID empresaId) throws Exception {
        Path archivo = Files.createTempFile("backup_empresa_" + empresaId + "_", ".sql");

        try (Connection conn = dataSource.getConnection();
                BufferedWriter writer = Files.newBufferedWriter(archivo)) {

            writer.write("-- Backup Empresa ID: " + empresaId);
            writer.newLine();
            writer.write("-- Fecha: " + LocalDateTime.now());
            writer.newLine();
            writer.newLine();

            for (Map.Entry<String, String> entry : TABLAS_TENANT.entrySet()) {
                String tabla = entry.getKey();
                String columnaEmpresa = entry.getValue();
                exportarTabla(conn, writer, tabla, columnaEmpresa, empresaId);
            }
        }
        return archivo;
    }

    private void exportarTabla(Connection conn, BufferedWriter writer,
            String tabla, String columnaEmpresa,
            UUID empresaId) throws Exception {
        String sql = "SELECT * FROM " + tabla + " WHERE " + columnaEmpresa + " = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, empresaId);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            writer.write("-- Tabla: " + tabla);
            writer.newLine();

            while (rs.next()) {
                StringBuilder insert = new StringBuilder("INSERT INTO ")
                        .append(tabla).append(" (");

                for (int i = 1; i <= cols; i++) {
                    insert.append(meta.getColumnName(i));
                    if (i < cols)
                        insert.append(", ");
                }
                insert.append(") VALUES (");

                for (int i = 1; i <= cols; i++) {
                    Object valor = rs.getObject(i);
                    if (valor == null) {
                        insert.append("NULL");
                    } else if (valor instanceof String || valor instanceof Timestamp
                            || valor instanceof Date) {
                        insert.append("'")
                                .append(valor.toString().replace("'", "''"))
                                .append("'");
                    } else {
                        insert.append(valor);
                    }
                    if (i < cols)
                        insert.append(", ");
                }
                insert.append(");");
                writer.write(insert.toString());
                writer.newLine();
            }
            writer.newLine();
        }
    }

    private Path comprimirArchivo(Path archivoSql) throws IOException {
        Path zipPath = Paths.get(archivoSql.toString().replace(".sql", ".zip"));
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            zos.putNextEntry(new ZipEntry(archivoSql.getFileName().toString()));
            Files.copy(archivoSql, zos);
            zos.closeEntry();
        }
        return zipPath;
    }

}
