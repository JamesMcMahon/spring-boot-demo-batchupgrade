package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Main application class for the Batch Upgrade Example application.
 * This Spring Boot application demonstrates batch processing capabilities
 * for importing user data from a CSV file into a database.
 * <p>
 * This class also excludes DataSourceAutoConfiguration. This is necessary because otherwise the application will
 * fail due to missing datasource configuration.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BatchUpgradeExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchUpgradeExampleApplication.class, args);
    }

}
