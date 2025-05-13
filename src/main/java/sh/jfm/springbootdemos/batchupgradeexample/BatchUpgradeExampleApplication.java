package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Main application class for the Batch Upgrade Example application.
 * This Spring Boot application demonstrates batch processing capabilities
 * for importing user data from a CSV file into a database.
 * <p>
 * This class also excludes DataSourceAutoConfiguration. This is necessary because otherwise the application will
 * attempt to autoconfigure a duplicate JobRepository bean.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BatchUpgradeExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchUpgradeExampleApplication.class, args);
    }

    /**
     * Creates an ApplicationRunner bean that automatically launches the batch job when the application starts.
     * This is necessary because Spring Batch jobs don't execute automatically when the DataSourceAutoConfiguration is
     * excluded.
     */
    @Bean
    public ApplicationRunner batchRunner(Job job, JobLauncher jobLauncher) {
        return args -> jobLauncher.run(job, new JobParameters());
    }
}
