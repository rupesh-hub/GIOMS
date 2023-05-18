package custom.keycloak.auth.config.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
  basePackages = "custom.keycloak.model",
  entityManagerFactoryRef = "userEntityManager",
  transactionManagerRef = "userTransactionManager")
public class PersistenceUserAutoConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }
}
