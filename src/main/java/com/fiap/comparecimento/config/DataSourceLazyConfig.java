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
 * Uses Cloud SQL Socket Factory to connect via public IP.
 * The Cloud SQL Auth Proxy handles encryption automatically.
 *
 * ddl-auto is set to "none" so startup never needs a live DB.
 * Tables must be created beforehand.
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

    @Value("${spring.datasource.ip-types:PUBLIC}")
    private String ipTypes;

    /**
     * HikariCP DataSource configured with Cloud SQL Socket Factory.
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
        config.addDataSourceProperty("cloudSqlRefreshStrategy", "lazy");

        return new HikariDataSource(config);
    }

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
