package sh.jfm.springbootdemos.batchupgradeexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Listener class that handles job completion notifications in the batch processing workflow.
 * This component monitors batch job execution and performs post-completion actions,
 * specifically querying and displaying processed user data from the database.
 */
@Component
public class JobCompleteLoggerListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompleteLoggerListener.class);

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

        try (BufferedReader reader = new BufferedReader(new FileReader("output/users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    log.info(String.format("Found <%s %s %s>", data[0], data[1], data[2]));
                }
            }
        } catch (IOException e) {
            log.error("Error reading users.csv file", e);
        }
    }
}

