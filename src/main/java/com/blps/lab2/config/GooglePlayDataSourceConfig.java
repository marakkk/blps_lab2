package com.blps.lab2.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.blps.lab2.repo.googleplay",
        entityManagerFactoryRef = "googleplayEntityManagerFactory",
        transactionManagerRef = "atomikosTransactionManager"
)
@EntityScan(basePackages = "com.blps.lab2.entities.googleplay")
public class GooglePlayDataSourceConfig {

    @Bean(name = "googleplayEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean googleplayEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("googleplayDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.blps.lab2.entities.googleplay")
                .persistenceUnit("googleplay")
                .properties(hibernateProperties())
                .build();
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform"); // Add Atomikos JTA platform
        return properties;
    }

    @Bean(name = "googleplayDataSource")
    public DataSource googleplayDataSource() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("googleplayDS");
        dataSource.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");

        Properties xaProperties = new Properties();
        xaProperties.setProperty("user", "admin");
        xaProperties.setProperty("password", "admin");
        xaProperties.setProperty("url", "jdbc:postgresql://localhost:5432/googleplay_db");

        dataSource.setXaProperties(xaProperties);
        dataSource.setMaxPoolSize(10);
        dataSource.setMinPoolSize(1);

        return dataSource;
    }

    @Bean(name = "googleplayTransactionManager")
    @DependsOn("userTransactionManager")
    public JpaTransactionManager googleplayTransactionManager(
            @Qualifier("googleplayEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }


}


