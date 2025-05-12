package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.core.*;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            jdbcTemplate.query("SELECT id, first_name, last_name FROM users",
                            (rs, row) -> String.format("Found <%d %s %s>", rs.getLong(1), rs.getString(2), rs.getString(3)))
                    .forEach(System.out::println);
        }
    }
}

