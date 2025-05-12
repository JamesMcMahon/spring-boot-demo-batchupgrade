package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Batch Upgrade Example application.
 * This Spring Boot application demonstrates batch processing capabilities
 * for importing user data from a CSV file into a database.
 */
@SpringBootApplication
public class BatchUpgradeExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchUpgradeExampleApplication.class, args);
	}

}
