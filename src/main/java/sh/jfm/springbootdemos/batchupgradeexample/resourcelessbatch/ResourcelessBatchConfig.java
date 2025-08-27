package sh.jfm.springbootdemos.batchupgradeexample.resourcelessbatch;

import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration class to avoid the usage of a database for batch processing.
 */
@Configuration
public class ResourcelessBatchConfig extends DefaultBatchConfiguration {

    private final ResourcelessJobRepository resourcelessJobRepository;

    public ResourcelessBatchConfig() {
        this.resourcelessJobRepository = new ResourcelessJobRepository();
    }

    /**
     * Use ResourcelessJobRepository for managing job execution metadata without persistent storage.
     * This implementation is suitable for scenarios where job execution data doesn't need to be persisted,
     * such as testing or simple batch processing workflows.
     *
     * @see <a href="https://docs.spring.io/spring-batch/reference/whatsnew.html#new-resourceless-job-repository">Spring Batch Documentation</a>
     */
    @Override
    public JobRepository jobRepository() throws BatchConfigurationException {
        return resourcelessJobRepository;
    }

    /**
     * Use ResourcelessJobExplorer for querying job execution metadata without persistent storage.
     */
    @Override
    public JobExplorer jobExplorer() throws BatchConfigurationException {
        return new ResourcelessJobExplorer(resourcelessJobRepository);
    }

    /**
     * Creates a ResourcelessTransactionManager bean for handling transactions without persistent storage.
     * This implementation is suitable for scenarios where transaction management isn't required,
     * such as testing or simple batch processing workflows.
     */
    @Bean
    protected PlatformTransactionManager getTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    /**
     * Creates an ApplicationRunner bean that automatically launches the batch job when the application starts.
     * This is necessary because {@link BatchAutoConfiguration} is not created if a bean extending
     * {@link DefaultBatchConfiguration} is present.
     */
    @Bean
    @ConditionalOnBooleanProperty(name = "spring.batch.job.enabled", matchIfMissing = true)
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(
            JobLauncher jobLauncher,
            JobExplorer jobExplorer,
            JobRepository jobRepository
    ) {
        return new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
    }
}
