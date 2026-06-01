package eccomerce.backend_eccomerce.backup.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import eccomerce.backend_eccomerce.backup.component.BackupJob;
import eccomerce.backend_eccomerce.backup.entity.BackupScheduleEntity;
import eccomerce.backend_eccomerce.backup.repository.BackupScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BackupSchedulerConfig {

    private static final String JOB_GROUP = "backups";
    private static final String ENTERPRISE_ID_KEY = "enterprise_id";

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private BackupScheduleRepository scheduleRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void reloadScheduledBackups() {
        log.info("Iniciando recarga de backups programados...");
        try {
            List<BackupScheduleEntity> schedules = scheduleRepository.findAll();

            log.info("Se encontraron {} configuraciones de backup", schedules.size());

            for (BackupScheduleEntity schedule : schedules) {
                if (Boolean.TRUE.equals(schedule.getActive())) {
                    if (!scheduler.checkExists(new JobKey("backup-job-" + schedule.getEnterpriseId(), JOB_GROUP))) {
                        scheduleJob(schedule.getEnterpriseId(), schedule.getCronExpression());
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("Error recargando backups programados desde la base de datos", e);
        }
        log.info("Recarga de backups finalizada");
    }

    public void programarBackup(UUID empresaId, String cronExpression) throws SchedulerException {
        scheduleRepository.findByEnterpriseId(empresaId).ifPresentOrElse(schedule -> {
            schedule.setCronExpression(cronExpression);
            schedule.setActive(true);
            schedule.setUpdatedAt(LocalDateTime.now());
            scheduleRepository.save(schedule);
        }, () -> {
            BackupScheduleEntity schedule = new BackupScheduleEntity();
            schedule.setEnterpriseId(empresaId);
            schedule.setCronExpression(cronExpression);
            schedule.setActive(true);
            schedule.setCreatedAt(LocalDateTime.now());
            schedule.setUpdatedAt(LocalDateTime.now());
            scheduleRepository.save(schedule);
        });

        if (scheduler.checkExists(new JobKey("backup-job-" + empresaId, JOB_GROUP))) {
            scheduler.deleteJob(new JobKey("backup-job-" + empresaId, JOB_GROUP));
        }

        scheduleJob(empresaId, cronExpression);
    }

    public void cancelarBackupProgramado(UUID empresaId) throws SchedulerException {
        scheduler.deleteJob(new JobKey("backup-job-" + empresaId, JOB_GROUP));
        scheduleRepository.findByEnterpriseId(empresaId).ifPresent(schedule -> {
            schedule.setActive(false);
            schedule.setUpdatedAt(LocalDateTime.now());
            scheduleRepository.save(schedule);
        });
    }

    private void scheduleJob(UUID empresaId, String cronExpression) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(BackupJob.class)
            .withIdentity("backup-job-" + empresaId, JOB_GROUP)
            .usingJobData(ENTERPRISE_ID_KEY, empresaId.toString())
            .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("backup-trigger-" + empresaId, JOB_GROUP)
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
            .build();

        scheduler.scheduleJob(job, trigger);
    }
}

