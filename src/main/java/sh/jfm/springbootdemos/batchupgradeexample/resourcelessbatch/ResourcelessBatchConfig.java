package sh.jfm.springbootdemos.batchupgradeexample.resourcelessbatch;

import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration class to avoid the usage of a database for batch processing.
 */
@SuppressWarnings("NullableProblems")
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

}
