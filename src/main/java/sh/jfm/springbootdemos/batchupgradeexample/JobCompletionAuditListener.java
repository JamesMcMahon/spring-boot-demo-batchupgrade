package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Listener component that audits Spring Batch job completion by recording job execution details
 * into a separate audit database.
 * <p>
 * <i>
 * Note: This audit functionality is redundant since Spring Batch already persists job execution
 * data in its own tables. This is implemented just for example purposes.
 * </i>
 */
@Component
public class JobCompletionAuditListener extends JobExecutionListenerSupport {

    private final JdbcTemplate auditJdbcTemplate;

    public JobCompletionAuditListener(@Qualifier("auditDataSource") DataSource auditDataSource) {
        this.auditJdbcTemplate = new JdbcTemplate(auditDataSource);
    }

    /**
     * Handles the after-job completion event by recording the job execution details
     * into the audit database. Records the job name, start time, end time, and final status.
     *
     * @param jobExecution The JobExecution instance containing details about the completed job
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        auditJdbcTemplate.update(
                "INSERT INTO audit_job (job_name, start_time, end_time, status) VALUES (?,?,?,?)",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStartTime(),
                jobExecution.getEndTime(),
                jobExecution.getStatus().toString()
        );
    }
}
