package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration class for Spring Batch processing setup.
 * This class configures the batch job for reading user data from CSV files,
 * processing the data, and writing it to a database.
 * It defines beans for readers, writers, processors, and job execution steps.
 */
@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemWriter<User> writer() {
        return new FlatFileItemWriterBuilder<User>()
                .name("userItemWriter")
                .resource(new FileSystemResource("output/users.csv"))
                .delimited()
                .names("id", "firstName", "lastName")
                .build();
    }

    @Bean
    public Job importUserJob(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JobCompleteLoggerListener listener
    ) {
        return new JobBuilder("importUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1(jobRepository, transactionManager))
                .end()
                .build();
    }

    private Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(reader())
                .processor(new UserNameProcessor())
                .writer(writer())
                .build();
    }

    private static FlatFileItemReader<User> reader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .resource(new ClassPathResource("users.csv"))
                .delimited()
                .names("id", "firstName", "lastName")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(User.class);
                }})
                .build();
    }
}
