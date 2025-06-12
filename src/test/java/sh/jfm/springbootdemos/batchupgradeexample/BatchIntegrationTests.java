package sh.jfm.springbootdemos.batchupgradeexample;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Integration test class for the Spring Batch CSV import process.
 * Tests the complete job execution flow including reading from CSV,
 * processing data, and writing to the database.
 */
@SpringBatchTest
@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchIntegrationTests {

    @Autowired
    private JobExplorer jobExplorer;

    private JdbcTemplate jdbcTemplate;

    /**
     * Configures the JDBC template with the provided DataSource.
     * This setup is required for database operations in the tests.
     *
     * @param dataSource the DataSource to be used for database operations
     */
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Tests the complete batch job execution flow.
     * This test:
     * 1. Cleans up the users table
     * 2. Executes the batch job
     * 3. Verifies the job completed successfully
     * 4. Confirms the correct number of records was inserted
     * 5. Validates the inserted data matches the expected values
     */
    @Test
    public void jobRunsAndInsertsCorrectData() {
        BatchUpgradeExampleApplication.main(new String[]{});

        var testExecution = jobExplorer.getJobExecutions(
                Objects.requireNonNull(jobExplorer.getLastJobInstance("importUserJob"))
        ).get(0);
        assertThat(testExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class))
                .isEqualTo(3);
        assertThat(jdbcTemplate.query(
                "SELECT * FROM users ORDER BY id",
                new BeanPropertyRowMapper<>(User.class)
        )).usingRecursiveComparison().isEqualTo(Arrays.asList(
                new User(1L, "JOHN", "DOE"),
                new User(2L, "JANE", "SMITH"),
                new User(3L, "ALAN", "TURING")
        ));
    }
}
