package eccomerce.backend_eccomerce.backup.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import eccomerce.backend_eccomerce.backup.enums.BackupState;
import eccomerce.backend_eccomerce.backup.enums.BackupType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "backups")
public class BackupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enterprise_id", nullable = false)
    private UUID enterpriseId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private BackupType type; // MANUAL, PROGRAMADO

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private BackupState state; // PENDIENTE, EN_PROGRESO, COMPLETADO, ERROR

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "message")
    private String message;

    // getters/setters...
}
