package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@SuppressWarnings("NullableProblems")
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DatabaselessBatchConfig extends DefaultBatchConfiguration {

    /**
     * Provides a no-operation DataSource implementation that doesn't support actual database connections.
     * This is a hacky workaround because the DataSource should not be used when configuring the ResourcelessJobRepository
     * but Spring Batch still requires a non-null DataSource for configuring a JobExplorer.
     */
    protected DataSource getDataSource() {
        return new NoOpDataSource();
    }

    @Override
    public JobExplorer jobExplorer() throws BatchConfigurationException {
        return new ResourcelessJobExplorer((ResourcelessJobRepository) jobRepository());
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
        return new ResourcelessJobRepository();
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
     * This is necessary because Spring Batch jobs don't execute automatically when the DataSourceAutoConfiguration is
     * excluded.
     */
    @Bean
    public ApplicationRunner batchRunner(Job job, JobLauncher jobLauncher) {
        return args -> jobLauncher.run(job, new JobParameters());
    }

    private static class NoOpDataSource implements DataSource {
        @Override
        public Connection getConnection() {
            throw new UnsupportedOperationException("This is a no-operation datasource - database connections are not supported");
        }

        @Override
        public Connection getConnection(String username, String password) {
            throw new UnsupportedOperationException("This is a no-operation datasource - database connections are not supported");
        }

        @Override
        public PrintWriter getLogWriter() {
            throw new UnsupportedOperationException("This is a no-operation datasource - log writer operations are not supported");
        }

        @Override
        public void setLogWriter(PrintWriter out) {
        }

        @Override
        public void setLoginTimeout(int seconds) {
        }

        @Override
        public int getLoginTimeout() {
            return 0;
        }

        @Override
        public <T> T unwrap(Class<T> iface) {
            throw new UnsupportedOperationException("This is a no-operation datasource - unwrap operations are not supported");
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) {
            return false;
        }

        @Override
        public Logger getParentLogger() {
            throw new UnsupportedOperationException("This is a no-operation datasource - logger operations are not supported");
        }
    }

    // TODO work around problems listed at https://github.com/spring-projects/spring-batch/issues/4718#issuecomment-2897323755
    private static class ResourcelessJobExplorer implements JobExplorer {

        private final ResourcelessJobRepository jobRepository;

        public ResourcelessJobExplorer(ResourcelessJobRepository jobRepository) {
            this.jobRepository = jobRepository;
        }

        private JobInstance getJobInstance() {
            return getJobExecution().getJobInstance();
        }

        private JobExecution getJobExecution() {
            return jobRepository.getLastJobExecution("", new JobParameters());
        }

        @Override
        public List<JobInstance> getJobInstances(String jobName, int start, int count) {
            return List.of(getJobInstance());
        }

        @Override
        public JobExecution getJobExecution(Long executionId) {
            return getJobExecution();
        }

        @Override
        public StepExecution getStepExecution(Long jobExecutionId, Long stepExecutionId) {
            return getJobExecution().getStepExecutions()
                    .stream()
                    .filter(stepExecution -> stepExecution.getJobExecutionId().equals(jobExecutionId))
                    .filter(stepExecution -> stepExecution.getId().equals(stepExecutionId))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public JobInstance getJobInstance(Long instanceId) {
            return getJobInstance();
        }

        @Override
        public List<JobExecution> getJobExecutions(JobInstance jobInstance) {
            return List.of(getJobExecution());
        }

        @Override
        public Set<JobExecution> findRunningJobExecutions(String jobName) {
            var jobExecution = getJobExecution();
            if (!jobExecution.isRunning()) {
                return Set.of();
            }
            return Set.of(jobExecution);
        }

        @Override
        public List<String> getJobNames() {
            return List.of(getJobInstance().getJobName());
        }

        @Override
        public List<JobInstance> findJobInstancesByJobName(String jobName, int start, int count) {
            var jobInstance = getJobInstance();
            if (!jobName.equals(jobInstance.getJobName())) {
                return List.of();
            }
            return List.of(jobInstance);
        }

        @Override
        public long getJobInstanceCount(String jobName) {
            return findJobInstancesByJobName(jobName, 0, 1).size();
        }
    }
}
