package eccomerce.backend_eccomerce.backup.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class BackupSessionDto {

    private UUID userId;

    private UUID enterpriseId;
    
}
