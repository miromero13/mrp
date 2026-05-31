package eccomerce.backend_eccomerce.backup.component;

import java.util.UUID;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eccomerce.backend_eccomerce.backup.enums.BackupType;
import eccomerce.backend_eccomerce.backup.service.BackupService;

@Component
public class BackupJob implements Job {

    @Autowired private BackupService backupService;

    @Override
    public void execute(JobExecutionContext context) {
        UUID empresaId = UUID.fromString(context.getJobDetail().getJobDataMap().getString("enterprise_id"));
        backupService.generarBackup(empresaId, BackupType.PROGRAMADO);
    }
}
