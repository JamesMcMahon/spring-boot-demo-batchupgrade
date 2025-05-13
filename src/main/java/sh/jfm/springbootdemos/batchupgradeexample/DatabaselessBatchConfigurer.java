package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Configures Spring Batch to use an in-memory Map-based job repository instead of a database.
 * This is achieved by providing an empty implementation of setDataSource.
 *
 * @see <a href="https://docs.spring.io/spring-batch/docs/4.3.x/reference/html/index-single.html#inMemoryRepository">Spring Batch 4.3 Documentation - In-Memory Repository</a>
 */
@Component
public class DatabaselessBatchConfigurer extends DefaultBatchConfigurer {

    @Override
    public void setDataSource(DataSource dataSource) {
    }

}
