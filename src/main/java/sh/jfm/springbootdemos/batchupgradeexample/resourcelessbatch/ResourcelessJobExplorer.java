package sh.jfm.springbootdemos.batchupgradeexample.resourcelessbatch;

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
        var jobInstance = getLastJobInstance(jobName);
        if (jobInstance == null) {
            return List.of();
        }
        return List.of(jobInstance);
    }

    @Override
    public long getJobInstanceCount(String jobName) {
        return findJobInstancesByJobName(jobName, 0, 1).size();
    }

    @Override
    public JobInstance getLastJobInstance(String jobName) {
        var jobInstance = getJobInstance();
        if (jobName.equals(jobInstance.getJobName())) {
            return jobInstance;
        }
        return null;
    }

    @Override
    public JobInstance getJobInstance(String jobName, JobParameters jobParameters) {
        return getJobInstance();
    }

    @Override
    public JobExecution getLastJobExecution(JobInstance jobInstance) {
        if (jobInstance.equals(getJobInstance())) {
            return getJobExecution();
        }
        return null;
    }

    private JobInstance getJobInstance() {
        return getJobExecution().getJobInstance();
    }

    private JobExecution getJobExecution() {
        return jobRepository.getLastJobExecution("", new JobParameters());
    }
}
