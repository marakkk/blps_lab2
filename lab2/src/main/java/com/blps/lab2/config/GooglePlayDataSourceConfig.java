package com.blps.lab2.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.blps.lab2.repo.googleplay",
        entityManagerFactoryRef = "googleplayEntityManagerFactory",
        transactionManagerRef = "atomikosTransactionManager"
)
public class GooglePlayDataSourceConfig {

    @Bean(name = "googleplayEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder googleplayEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), Collections.emptyMap(), null);
    }

    @Bean(name = "googleplayEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean googleplayEntityManagerFactory(
            @Qualifier("googleplayEntityManagerFactoryBuilder") EntityManagerFactoryBuilder builder,
            @Qualifier("googleplayDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.blps.lab2.entities.googleplay")
                .persistenceUnit("googleplay")
                .properties(hibernateProperties())
                .build();
    }

    private Map<String, Object> hibernateProperties() {
        //TODO: move to file
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform");
        return properties;
    }

    @Bean(name = "googleplayDataSource")
    public DataSource googleplayDataSource() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("googleplayDS");
        dataSource.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");

        //TODO: move to file
        Properties xaProperties = new Properties();
        xaProperties.setProperty("user", "admin");
        xaProperties.setProperty("password", "admin");
        xaProperties.setProperty("url", "jdbc:postgresql://localhost:5432/googleplay_db");

        dataSource.setXaProperties(xaProperties);
        dataSource.setMaxPoolSize(10);
        dataSource.setMinPoolSize(1);

        return dataSource;
    }

}
