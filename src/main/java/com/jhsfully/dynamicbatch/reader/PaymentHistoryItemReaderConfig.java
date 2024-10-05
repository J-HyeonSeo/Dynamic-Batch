package com.jhsfully.dynamicbatch.reader;

import com.jhsfully.dynamicbatch.dto.PaymentHistoryDto;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@Configuration
public class PaymentHistoryItemReaderConfig {

    private static final int CHUNK_SIZE = 100;

    private final DataSource sourceDataSource;

    public PaymentHistoryItemReaderConfig(
        @Qualifier("sourceDataSource") DataSource sourceDataSource
    ) {
        this.sourceDataSource = sourceDataSource;
    }

    @Bean
    @StepScope
    public ItemReader<PaymentHistoryDto> paymentHistoryItemReader(
        @Value("#{jobParameters['inputDate']}") String inputDate
    ) throws Exception {

        Thread.sleep(10000);

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("inputDate", inputDate);

        return new JdbcPagingItemReaderBuilder<PaymentHistoryDto>()
            .name("paymentHistoryItemReader")
            .pageSize(CHUNK_SIZE)
            .dataSource(sourceDataSource)
            .rowMapper(new BeanPropertyRowMapper<>(PaymentHistoryDto.class))
            .queryProvider(createQueryProvider())
            .parameterValues(parameterValues)
            .build();
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception{

        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(sourceDataSource);
        queryProvider.setSelectClause("user_id, sum(price) as price");
        queryProvider.setFromClause("from payment_history");
        queryProvider.setWhereClause("where DATE(paid_at) = :inputDate");
        queryProvider.setGroupClause("group by user_id");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("user_id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

}
