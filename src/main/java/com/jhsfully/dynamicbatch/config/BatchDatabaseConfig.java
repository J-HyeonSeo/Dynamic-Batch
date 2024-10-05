package com.jhsfully.dynamicbatch.config;

import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@EnableJpaRepositories(
    basePackages = "com.jhsfully.dynamicbatch.repository",
    entityManagerFactoryRef = "dataEntityManager",
    transactionManagerRef = "jpaTransactionManager"
)
public class BatchDatabaseConfig {

    @Primary //SpringBatch에서 기본적으로 가져다가 사용가능하게
    @Bean("batchDataSource")
    @ConfigurationProperties(prefix = "batch.meta.datasource")
    public DataSource batchDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary //SpringBatch에서 기본적으로 가져다가 사용가능하게
    @Bean("batchTransactionManager")
    public PlatformTransactionManager batchTransactionManager() {
        return new DataSourceTransactionManager(batchDataSource());
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean dataEntityManager() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(batchDataSource());
        factory.setPackagesToScan("com.jhsfully.dynamicbatch.entity");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.show_sql", "true");
        factory.setJpaPropertyMap(properties);

        return factory;
    }

    @Bean("jpaTransactionManager")
    public PlatformTransactionManager jpaTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(dataEntityManager().getObject());
        return transactionManager;
    }

}
