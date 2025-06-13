package sh.jfm.springbootdemos.batchupgradeexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Listener class that handles job completion notifications in the batch processing workflow.
 * This component monitors batch job execution and performs post-completion actions,
 * specifically querying and displaying processed user data from the database.
 */
@Component
public class JobCompleteLoggerListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompleteLoggerListener.class);
    private final JdbcTemplate userJdbcTemplate;

    public JobCompleteLoggerListener(@Qualifier("userDataSource") DataSource userDataSource) {
        this.userJdbcTemplate = new JdbcTemplate(userDataSource);
    }

    /**
     * Executes after the job completion. If the job completed successfully,
     * queries the database to display all processed users.
     *
     * @param jobExecution The JobExecution instance containing information about the completed job
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            return;
        }
        userJdbcTemplate.query(
                        "SELECT id, first_name, last_name FROM users",
                        (rs, row) -> String.format("Found <%d %s %s>", rs.getLong(1), rs.getString(2), rs.getString(3))
                )
                .forEach(log::info);
    }
}

