package com.jhsfully.dynamicbatch.job;

import com.jhsfully.dynamicbatch.dto.PaymentAdjustmentDto;
import com.jhsfully.dynamicbatch.dto.PaymentHistoryDto;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.concurrent.Future;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DayAdjustmentJobConfig {

    private static final int CHUNK_SIZE = 100;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ItemReader<PaymentHistoryDto> paymentHistoryItemReader;
    private final AsyncItemProcessor<PaymentHistoryDto, PaymentAdjustmentDto> adjustmentAsyncItemProcessor;
    private final AsyncItemWriter<PaymentAdjustmentDto> paymentAdjustmentAsyncItemWriter;
    private final PlatformTransactionManager targetTransactionManager;

    public DayAdjustmentJobConfig(
        JobBuilderFactory jobBuilderFactory,
        StepBuilderFactory stepBuilderFactory,
        ItemReader<PaymentHistoryDto> paymentHistoryItemReader,
        AsyncItemProcessor<PaymentHistoryDto, PaymentAdjustmentDto> adjustmentAsyncItemProcessor,
        AsyncItemWriter<PaymentAdjustmentDto> paymentAdjustmentAsyncItemWriter,
        @Qualifier("targetTransactionManager") PlatformTransactionManager targetTransactionManager
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.paymentHistoryItemReader = paymentHistoryItemReader;
        this.adjustmentAsyncItemProcessor = adjustmentAsyncItemProcessor;
        this.paymentAdjustmentAsyncItemWriter = paymentAdjustmentAsyncItemWriter;
        this.targetTransactionManager = targetTransactionManager;
    }

    @Bean
    public Job dayAdjustmenJob() {
        return jobBuilderFactory.get("dayAdjustmentJob")
            .start(dayAdjustmentStep())
            .build();
    }

    @Bean
    public Step dayAdjustmentStep() {
        return stepBuilderFactory.get("dayAdjustmentStep")
            .<PaymentHistoryDto, Future<PaymentAdjustmentDto>>chunk(CHUNK_SIZE)
            .reader(paymentHistoryItemReader)
            .processor(adjustmentAsyncItemProcessor)
            .writer(paymentAdjustmentAsyncItemWriter)
            .faultTolerant()
            .retry(SQLException.class)
            .retryLimit(3)
            .skip(DataIntegrityViolationException.class)
            .skipLimit(5)
            .transactionManager(targetTransactionManager)
            .build();
    }


}
