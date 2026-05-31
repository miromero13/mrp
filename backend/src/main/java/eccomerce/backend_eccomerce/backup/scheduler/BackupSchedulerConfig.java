package eccomerce.backend_eccomerce.backup.scheduler;

import java.util.UUID;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import eccomerce.backend_eccomerce.backup.component.BackupJob;

@Configuration
public class BackupSchedulerConfig {

    @Autowired 
    private Scheduler scheduler;

    // Crear un job programado por empresa
    public void programarBackup(UUID empresaId, String cronExpression) throws SchedulerException {

        JobDetail job = JobBuilder.newJob(BackupJob.class)
            .withIdentity("backup-job-" + empresaId, "backups")
            .usingJobData("empresaId", empresaId.toString())
            .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("backup-trigger-" + empresaId, "backups")
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
            .build();

        scheduler.scheduleJob(job, trigger);
    }

    public void cancelarBackupProgramado(UUID empresaId) throws SchedulerException {
        scheduler.deleteJob(new JobKey("backup-job-" + empresaId, "backups"));
    }
}

