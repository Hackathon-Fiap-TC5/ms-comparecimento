package com.fiap.comparecimento.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

/**
 * Manual JPA/DataSource configuration for Cloud Run.
 *
 * Uses LazyConnectionDataSourceProxy so Hibernate can build its
 * SessionFactory without opening a real JDBC connection. The actual
 * connection to Cloud SQL only happens on the first query.
 *
 * ddl-auto is set to "none" so startup never needs a live DB.
 * Tables must be created beforehand (e.g. via a migration tool or manually).
 */
@Configuration
@Profile("!local")
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.fiap.comparecimento.infrastructure.database.repositories",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class DataSourceLazyConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverClassName;

    @Value("${spring.datasource.cloud-sql-instance}")
    private String cloudSqlInstance;

    @Value("${spring.datasource.ip-types:PRIVATE}")
    private String ipTypes;

    /**
     * The real HikariCP DataSource configured with Cloud SQL Socket Factory.
     * Uses "lazy" refresh strategy as recommended by Google for serverless.
     */
    private DataSource realDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName(dbDriverClassName);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(0);
        config.setConnectionTimeout(30000);
        config.setInitializationFailTimeout(-1);

        // Cloud SQL Socket Factory configuration
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", cloudSqlInstance);
        config.addDataSourceProperty("ipTypes", ipTypes);
        // "lazy" refresh avoids background CPU usage in serverless environments
        config.addDataSourceProperty("cloudSqlRefreshStrategy", "lazy");

        return new HikariDataSource(config);
    }

    /**
     * Wraps the real DataSource in a lazy proxy. Hibernate will receive
     * this proxy during SessionFactory creation but no actual JDBC
     * connection is opened until a SQL statement is executed.
     */
    @Bean
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(realDataSource());
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.fiap.comparecimento.infrastructure.database.entities");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(false);
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        // "none" = don't try to connect to the DB during startup
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.jdbc.time_zone", "UTC");
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
