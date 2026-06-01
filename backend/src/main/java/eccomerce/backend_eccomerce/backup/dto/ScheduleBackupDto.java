package eccomerce.backend_eccomerce.backup.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class ScheduleBackupDto {

    private UUID userId;

    private UUID enterpriseId;

    private String cronExpression;

}
