package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Main application class for the Batch Upgrade Example application.
 * This Spring Boot application demonstrates batch processing capabilities
 * for importing user data from a CSV file into a database.
 * <p>
 * The application excludes DataSourceAutoConfiguration to run without a database connection.
 * This is useful for scenarios where we want to process files without persisting to a database,
 * or when running simple batch operations that don't require database storage.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BatchUpgradeExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchUpgradeExampleApplication.class, args);
    }

    //region Databaseless Batch Configuration
    /*
      The following beans configure a minimal batch processing environment without a database:
      - ResourcelessTransactionManager: Provides transaction management without a real transaction store
      - ResourcelessJobRepository: Provides job execution context without persistent storage
      - TaskExecutorJobLauncher: Handles job execution without depending on database resources
      <p>
      This configuration is suitable for simple batch operations that don't require
      persistent job repository storage or transaction management.
      Reference: <a href="https://stackoverflow.com/a/79492398">Stack Overflow</a>
     */

    /**
     * Creates a ResourcelessTransactionManager bean for handling transactions without persistent storage.
     * This implementation is suitable for scenarios where transaction management isn't required,
     * such as testing or simple batch processing workflows.
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    /**
     * Creates a ResourcelessJobRepository bean for managing job execution metadata without persistent storage.
     * This implementation is suitable for scenarios where job execution data doesn't need to be persisted,
     * such as testing or simple batch processing workflows.
     *
     * @see <a href="https://docs.spring.io/spring-batch/reference/whatsnew.html#new-resourceless-job-repository">Spring Batch Documentation</a>
     */
    @Bean
    public JobRepository jobRepository() {
        return new ResourcelessJobRepository();
    }

    /**
     * Creates a JobLauncher bean responsible for executing batch jobs.
     * The JobLauncher is a crucial component in Spring Batch that handles the execution
     * of jobs, including their initialization and launch.
     * We need to manually wire this when we exclude DataSourceAutoConfiguration.
     */
    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
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
    //endregion
}
