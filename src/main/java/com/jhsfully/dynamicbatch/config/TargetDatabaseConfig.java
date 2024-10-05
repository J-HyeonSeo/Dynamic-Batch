package com.jhsfully.dynamicbatch.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TargetDatabaseConfig {

    @Bean("targetDataSource")
    @ConfigurationProperties(prefix = "batch.target.datasource")
    public DataSource targetDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("targetTransactionManager")
    PlatformTransactionManager targetTransactionManager() {
        return new DataSourceTransactionManager(targetDataSource());
    }

}
