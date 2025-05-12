package sh.jfm.springbootdemos.batchupgradeexample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBatchTest
@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchCsvApplicationTests {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testJobRunsAndInsertsData() throws Exception {
        this.jdbcTemplate.update("delete from users");

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
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
