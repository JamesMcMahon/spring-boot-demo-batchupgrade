package sh.jfm.springbootdemos.batchupgradeexample;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Integration test class for the Spring Batch file processing.
 * Tests the complete job execution flow including reading from input CSV,
 * processing data, and writing to an output CSV file.
 */
@SpringBatchTest
@SpringBootTest
public class BatchIntegrationTests {

    @Autowired
    private JobRepository jobRepository;

    /**
     * Tests the complete batch job execution flow.
     * This test:
     * 1. Executes the batch job
     * 2. Verifies the job completed successfully
     * 3. Reads the generated output/users.csv file
     * 4. Confirms the correct number of records was written
     * 5. Validates the output data matches the expected values
     *
     * @throws Exception if any error occurs during job execution or file reading
     */
    @Test
    public void jobRunsAndInsertsCorrectData() throws Exception {
        var testExecution = Objects.requireNonNull(
                jobRepository.getLastJobExecution("importUserJob", new JobParameters())
        );
        assertThat(testExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        List<User> users = readUsersFromCsv();
        assertThat(users.size()).isEqualTo(3);
        assertThat(users).usingRecursiveComparison().isEqualTo(Arrays.asList(
                new User(1L, "JOHN", "DOE"),
                new User(2L, "JANE", "SMITH"),
                new User(3L, "ALAN", "TURING")
        ));
    }

    private static List<User> readUsersFromCsv() throws Exception {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("output/users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    users.add(new User(Long.parseLong(data[0]), data[1], data[2]));
                }
            }
        }
        return users;
    }
}
