package sh.jfm.springbootdemos.batchupgradeexample.resourcelessbatch;

import jakarta.annotation.Nullable;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        return getSingletonJobInstance()
                .stream()
                .filter(instance -> jobName.equals(instance.getJobName()))
                .toList();
    }

    @Override
    @Nullable
    public JobExecution getJobExecution(@Nullable Long executionId) {
        return getSingletonJobExecution()
                .filter(execution -> execution.getId().equals(executionId))
                .orElse(null);
    }

    @Override
    @Nullable
    public StepExecution getStepExecution(@Nullable Long jobExecutionId, @Nullable Long stepExecutionId) {
        return getSingletonJobExecution()
                .filter(execution -> execution.getId().equals(jobExecutionId))
                .map(JobExecution::getStepExecutions)
                .flatMap(steps -> steps.stream()
                        .filter(step -> step.getJobExecutionId().equals(jobExecutionId))
                        .filter(step -> step.getId().equals(stepExecutionId))
                        .findFirst())
                .orElse(null);
    }

    @Override
    @Nullable
    public JobInstance getJobInstance(@Nullable Long instanceId) {
        return getSingletonJobInstance()
                .filter(instance -> instance.getId().equals(instanceId))
                .orElse(null);
    }

    @Override
    public List<JobExecution> getJobExecutions(JobInstance jobInstance) {
        return getSingletonJobExecution()
                .stream()
                .filter(execution -> jobInstance.equals(execution.getJobInstance()))
                .toList();
    }

    @Override
    public Set<JobExecution> findRunningJobExecutions(@Nullable String jobName) {
        return getSingletonJobExecution()
                .stream()
                .filter(JobExecution::isRunning)
                .collect(Collectors.toSet());
    }

    @Override
    public List<String> getJobNames() {
        return getSingletonJobInstance()
                .stream()
                .map(JobInstance::getJobName)
                .toList();
    }

    @Override
    public List<JobInstance> findJobInstancesByJobName(@Nullable String jobName, int start, int count) {
        return Optional.ofNullable(getLastJobInstance(jobName))
                .stream()
                .toList();
    }

    @Override
    public long getJobInstanceCount(@Nullable String jobName) {
        return findJobInstancesByJobName(jobName, 0, 1).size();
    }

    @Override
    @Nullable
    public JobInstance getLastJobInstance(@Nullable String jobName) {
        return getSingletonJobInstance()
                .filter(instance -> instance.getJobName().equals(jobName))
                .orElse(null);
    }

    @Override
    @Nullable
    public JobInstance getJobInstance(String jobName, JobParameters jobParameters) {
        return getSingletonJobInstance().orElse(null);
    }

    @Override
    @Nullable
    public JobExecution getLastJobExecution(JobInstance jobInstance) {
        return getSingletonJobInstance()
                .filter(jobInstance::equals)
                .flatMap(instance -> getSingletonJobExecution())
                .orElse(null);
    }

    private Optional<JobInstance> getSingletonJobInstance() {
        return getSingletonJobExecution()
                .map(JobExecution::getJobInstance);
    }

    private Optional<JobExecution> getSingletonJobExecution() {
        // parameters don't matter here as there is only one execution for a ResourcelessJobRepository
        //noinspection OptionalOfNullableMisuse
        return Optional.ofNullable(jobRepository.getLastJobExecution("", new JobParameters()));
    }
}
