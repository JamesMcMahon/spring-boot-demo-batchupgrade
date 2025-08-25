package sh.jfm.springbootdemos.batchupgradeexample.resourcelessbatch;

import jakarta.annotation.Nullable;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;

import java.util.List;
import java.util.Set;

/**
 * JobExplore based around {@link ResourcelessJobRepository}.
 * <p>
 * Designed to work around design problems <a href="https://github.com/spring-projects/spring-batch/issues/4718#issuecomment-2897323755">resolved in Spring Batch 6</a>
 */
class ResourcelessJobExplorer implements JobExplorer {

    private final ResourcelessJobRepository jobRepository;

    public ResourcelessJobExplorer(ResourcelessJobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<JobInstance> getJobInstances(String jobName, int start, int count) {
        var jobInstance = getJobInstance();
        if (jobInstance == null || !jobName.equals(jobInstance.getJobName())) {
            return List.of();
        }
        return List.of(jobInstance);
    }

    @Override
    @Nullable
    public JobExecution getJobExecution(@Nullable Long executionId) {
        var jobExecution = getJobExecution();
        if (jobExecution == null || jobExecution.getId().equals(executionId)) {
            return null;
        }
        return jobExecution;
    }

    @Override
    @Nullable
    public StepExecution getStepExecution(@Nullable Long jobExecutionId, @Nullable Long stepExecutionId) {
        var jobExecution = getJobExecution();
        if (jobExecution == null || jobExecution.getId().equals(jobExecutionId)) {
            return null;
        }
        return jobExecution.getStepExecutions()
                .stream()
                .filter(stepExecution -> stepExecution.getJobExecutionId().equals(jobExecutionId))
                .filter(stepExecution -> stepExecution.getId().equals(stepExecutionId))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Nullable
    public JobInstance getJobInstance(@Nullable Long instanceId) {
        JobInstance jobInstance = getJobInstance();
        if (jobInstance == null || jobInstance.getId().equals(instanceId)) {
            return null;
        }
        return jobInstance;
    }

    @Override
    public List<JobExecution> getJobExecutions(JobInstance jobInstance) {
        var jobExecution = getJobExecution();
        if (jobExecution == null || !jobInstance.equals(jobExecution.getJobInstance())) {
            return List.of();
        }
        return List.of(jobExecution);
    }

    @Override
    public Set<JobExecution> findRunningJobExecutions(@Nullable String jobName) {
        var jobExecution = getJobExecution();
        if (jobExecution == null || !jobExecution.isRunning()) {
            return Set.of();
        }
        return Set.of(jobExecution);
    }

    @Override
    public List<String> getJobNames() {
        var jobInstance = getJobInstance();
        if (jobInstance == null) {
            return List.of();
        }
        return List.of(jobInstance.getJobName());
    }

    @Override
    public List<JobInstance> findJobInstancesByJobName(@Nullable String jobName, int start, int count) {
        var jobInstance = getLastJobInstance(jobName);
        if (jobInstance == null) {
            return List.of();
        }
        return List.of(jobInstance);
    }

    @Override
    public long getJobInstanceCount(@Nullable String jobName) {
        return findJobInstancesByJobName(jobName, 0, 1).size();
    }

    @Override
    @Nullable
    public JobInstance getLastJobInstance(@Nullable String jobName) {
        var jobInstance = getJobInstance();
        if (jobInstance == null || !jobInstance.getJobName().equals(jobName)) {
            return null;
        }
        return jobInstance;
    }

    @Override
    @Nullable
    public JobInstance getJobInstance(String jobName, JobParameters jobParameters) {
        return getJobInstance();
    }

    @Override
    @Nullable
    public JobExecution getLastJobExecution(JobInstance jobInstance) {
        if (jobInstance.equals(getJobInstance())) {
            return getJobExecution();
        }
        return null;
    }

    @Nullable
    private JobInstance getJobInstance() {
        var jobExecution = getJobExecution();
        if (jobExecution == null) {
            return null;
        }
        return jobExecution.getJobInstance();
    }

    @SuppressWarnings("DataFlowIssue")
    @Nullable
    private JobExecution getJobExecution() {
        // parameters don't matter here as there is only one execution for a ResourcelessJobRepository
        return jobRepository.getLastJobExecution("", new JobParameters());
    }
}
