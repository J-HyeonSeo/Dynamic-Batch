package com.jhsfully.dynamicbatch.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SourceDatabaseConfig {

    @Bean("sourceDataSource")
    @ConfigurationProperties(prefix = "batch.source.datasource")
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("sourceTransactionManager")
    PlatformTransactionManager sourceTransactionManager() {
        return new DataSourceTransactionManager(sourceDataSource());
    }

}
