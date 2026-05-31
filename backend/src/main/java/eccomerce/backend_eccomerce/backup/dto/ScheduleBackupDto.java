package eccomerce.backend_eccomerce.backup.dto;

import lombok.Data;

@Data
public class ScheduleBackupDto {
    private String cronExpression;

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
