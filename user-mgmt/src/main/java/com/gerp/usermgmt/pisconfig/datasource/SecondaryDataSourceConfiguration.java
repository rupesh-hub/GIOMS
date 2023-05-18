//package com.gerp.usermgmt.pisconfig.datasource;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//		basePackages = {"com.gerp.usermgmt.pisconfig.repo"},
//		entityManagerFactoryRef = "secondaryEntityManagerFactory",
//		transactionManagerRef = "secondaryTransactionManager")
//public class SecondaryDataSourceConfiguration {
//
//	@Bean
//	@ConfigurationProperties(prefix="spring.second-datasource")
//	public DataSource secondaryDataSource() {
//		return DataSourceBuilder.create().build();
//	}
//
//	@Bean
//	public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
//			EntityManagerFactoryBuilder builder, @Qualifier("secondaryDataSource") DataSource dataSource) {
//		Map<String, Object> properties = new HashMap<>();
////		properties.put("hibernate.dialect", "org.postgresql.Driver");
//		properties.put("hibernate.hbm2ddl.auto", "none");
////		TimeZone timeZone = TimeZone.getTimeZone("Asia/Kathmandu");
////		TimeZone.setDefault(timeZone);
////		properties.put("oracle.jdbc.timezoneAsRegion", false);
//		return builder.dataSource(dataSource).packages("com.gerp.usermgmt.pisconfig.model").persistenceUnit("secondary")
//				.properties(properties)
//				.build();
//	}
//
//	@Bean
//	public PlatformTransactionManager secondaryTransactionManager(
//			@Qualifier("secondaryEntityManagerFactory") EntityManagerFactory barEntityManagerFactory) {
//		return new JpaTransactionManager(barEntityManagerFactory);
//	}
//}
