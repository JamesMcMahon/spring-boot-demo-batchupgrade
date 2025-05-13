package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * The following beans configure a minimal batch processing environment without a database:
 * <ul>
 * <li>ResourcelessTransactionManager: Provides transaction management without a real transaction store</li>
 * <li>ResourcelessJobRepository: Provides job execution context without persistent storage</li>
 * <li>TaskExecutorJobLauncher: Handles job execution without depending on database resources</li>
 * </ul>
 * <p>
 * This configuration is suitable for simple batch operations that don't require
 * persistent job repository storage or transaction management.
 *
 * @see <a href="https://stackoverflow.com/a/79492398">Stack Overflow</a>
 * @see org.springframework.batch.core.repository.JobRepository
 */
@Configuration
public class DatabaselessBatchConfig {

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
}
