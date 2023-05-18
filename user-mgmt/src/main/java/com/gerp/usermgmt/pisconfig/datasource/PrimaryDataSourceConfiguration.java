package com.gerp.usermgmt.pisconfig.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
  basePackages = {"com.gerp.usermgmt.repo","com.gerp.usermgmt.model"},
  entityManagerFactoryRef = "primaryEntityManagerFactory",
  transactionManagerRef = "primaryTransactionManager")
public class PrimaryDataSourceConfiguration {

    @Value("${spring.datasource.hikari.minimum-idle}")
    private Integer minimumIdleTime;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private Integer maximumPoolSize;

    @Value("${spring.datasource.hikari.connection-timeout}")
    private Integer connectionTimeout;

    @Primary
    @Bean
    public DataSource primaryDataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("primaryDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.gerp.usermgmt.model")
                .persistenceUnit("primary")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory barEntityManagerFactory) {
        return new JpaTransactionManager(barEntityManagerFactory);
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix="spring.datasource")
    public HikariConfig hikariConfig() {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setMaximumPoolSize(maximumPoolSize);
            hikariConfig.setConnectionTimeout(connectionTimeout);
            hikariConfig.setMaxLifetime(1800);
            hikariConfig.setLeakDetectionThreshold(600000);
            hikariConfig.setMinimumIdle(minimumIdleTime);
            hikariConfig.setIdleTimeout(600000);
        return  hikariConfig;
    }

}
