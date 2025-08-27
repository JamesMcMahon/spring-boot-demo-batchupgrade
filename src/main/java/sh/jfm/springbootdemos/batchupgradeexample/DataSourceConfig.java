package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Declares two {@link javax.sql.DataSource DataSources}.
 *
 * <ul>
 *   <li><strong>userDataSource</strong> – user data written by the job</li>
 *   <li><strong>auditDataSource</strong> – separate audit schema</li>
 * </ul>
 * <p>
 * Spring Boot 3 autoconfigures only one DataSource.
 * If you use more than one DataSource, you will need to manually initialize schemas as demoed in this class.
 * <p>
 * If you need specific connection pooling settings, you should construct a DataSource using {@code DataSourceProperties}.
 * The audit DataSource below demonstrates this approach.
 *
 * @see <a href="https://docs.spring.io/spring-boot/how-to/data-access.html#howto.data-access.configure-two-datasources">Boot 3 reference guide § “Data Access > Configure Two DataSources”</a>
 */
@Configuration
public class DataSourceConfig {

    //region User DataSource Configuration

    /**
     * DataSource for user data written by the job.
     *
     * @see <a href="https://docs.spring.io/spring-boot/how-to/data-access.html#howto.data-access.configure-custom-datasource">Configure a Custom DataSource</a>
     */
    @Bean(name = "userDataSource")
    @ConfigurationProperties("app.datasource.user")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Initialize user schema.
     * <p>
     * Boot’s automatic SQL initializer is disabled because we have >1 DataSource.
     */
    @Bean
    public DataSourceInitializer userDataSourceInitializer(@Qualifier("userDataSource") DataSource dataSource) {
        return initializeSchema(dataSource, "schema-user.sql");
    }
    //endregion

    //region Audit DataSource Configuration

    /**
     * Use a {@link org.springframework.boot.autoconfigure.jdbc DataSourceProperties} to allow the use of specific
     * connection pool configuration.
     *
     * @see <a href="https://docs.spring.io/spring-boot/how-to/data-access.html#howto.data-access.configure-custom-datasource">Configure a Custom DataSource</a>
     */
    @Bean
    @ConfigurationProperties("app.datasource.audit")
    public DataSourceProperties auditDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Creates and configures the audit DataSource using properties defined in auditDataSourceProperties.
     * Additional configuration properties are bound from 'app.datasource.audit.configuration' prefix.
     */
    @Bean(name = "auditDataSource")
    @ConfigurationProperties("app.datasource.audit.configuration")
    public DataSource auditDataSource() {
        return auditDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    /**
     * Initialize audit schema.
     * <p>
     * Boot’s automatic SQL initializer is disabled because we have >1 DataSource.
     */
    @Bean
    public DataSourceInitializer auditDataSourceInitializer(@Qualifier("auditDataSource") DataSource dataSource) {
        return initializeSchema(dataSource, "schema-audit.sql");
    }
    //endregion

    /**
     * DRY helper that creates a DataSourceInitializer executing a single script.
     * Keeps the two public DataSourceInitializer beans concise.
     */
    private static DataSourceInitializer initializeSchema(DataSource dataSource, String path) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(path));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
