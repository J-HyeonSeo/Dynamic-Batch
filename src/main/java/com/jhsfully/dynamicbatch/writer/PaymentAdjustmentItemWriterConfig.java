package com.jhsfully.dynamicbatch.writer;

import com.jhsfully.dynamicbatch.dto.PaymentAdjustmentDto;
import javax.sql.DataSource;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentAdjustmentItemWriterConfig {

    private final DataSource targetDataSource;

    public PaymentAdjustmentItemWriterConfig(
        @Qualifier("targetDataSource") DataSource targetDataSource
    ) {
        this.targetDataSource = targetDataSource;
    }

    @Bean
    public AsyncItemWriter<PaymentAdjustmentDto> paymentAdjustmentAsyncItemWriter() {
        AsyncItemWriter<PaymentAdjustmentDto> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(paymentAdjustmentItemWriter());

        return asyncItemWriter;
    }

    @Bean
    public ItemWriter<PaymentAdjustmentDto> paymentAdjustmentItemWriter() {
        return new JdbcBatchItemWriterBuilder<PaymentAdjustmentDto>()
            .dataSource(targetDataSource)
            .sql("insert into payment_adjustment (user_id, adjustmented_price, adjustmented_at) "
                + "values (:userId, :adjustmentedPrice, :adjustmentedAt)")
            .beanMapped()
            .build();
    }

}
