package com.blps.lab2.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.blps.lab2.repo.payments",
        entityManagerFactoryRef = "paymentsEntityManagerFactory",
        transactionManagerRef = "atomikosTransactionManager"
)
@EntityScan(basePackages = "com.blps.lab2.entities.payments")
public class PaymentDataSourceConfig {

    @Bean(name = "paymentsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean paymentsEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("paymentsDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.blps.lab2.entities.payments")
                .persistenceUnit("payments")
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

    @Bean(name = "paymentsDataSource")
    public DataSource paymentsDataSource() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("paymentsDS");
        dataSource.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");

        Properties xaProperties = new Properties();
        xaProperties.setProperty("user", "admin");
        xaProperties.setProperty("password", "admin");
        xaProperties.setProperty("url", "jdbc:postgresql://localhost:5432/payments_db");

        dataSource.setXaProperties(xaProperties);
        dataSource.setMaxPoolSize(10);
        dataSource.setMinPoolSize(1);

        return dataSource;
    }

    @Bean(name = "paymentsTransactionManager")
    @DependsOn("userTransactionManager")
    public JpaTransactionManager paymentsTransactionManager(
            @Qualifier("paymentsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}

