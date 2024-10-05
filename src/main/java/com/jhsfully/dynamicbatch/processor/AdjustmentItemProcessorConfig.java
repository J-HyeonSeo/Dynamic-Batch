package com.jhsfully.dynamicbatch.processor;

import com.jhsfully.dynamicbatch.dto.PaymentAdjustmentDto;
import com.jhsfully.dynamicbatch.dto.PaymentHistoryDto;
import com.jhsfully.dynamicbatch.entity.AdjustmentFactor;
import com.jhsfully.dynamicbatch.repository.AdjustmentFactorRepository;
import java.time.LocalDateTime;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class AdjustmentItemProcessorConfig {

    private final AdjustmentFactorRepository adjustmentFactorRepository;
    private final TaskExecutor adjustmentTaskExecutor;

    public AdjustmentItemProcessorConfig(
        AdjustmentFactorRepository adjustmentFactorRepository,
        TaskExecutor adjustmentTaskExecutor
    ) {
        this.adjustmentFactorRepository = adjustmentFactorRepository;
        this.adjustmentTaskExecutor = adjustmentTaskExecutor;
    }

    @Bean
    public AsyncItemProcessor<PaymentHistoryDto, PaymentAdjustmentDto> adjustmentAsyncItemProcessor() {

        AsyncItemProcessor<PaymentHistoryDto, PaymentAdjustmentDto> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(adjustmentItemProcessor());
        asyncItemProcessor.setTaskExecutor(adjustmentTaskExecutor);

        return asyncItemProcessor;
    }

    @Bean
    public ItemProcessor<PaymentHistoryDto, PaymentAdjustmentDto> adjustmentItemProcessor() {
        return paymentHistory -> {
            float adjustmentFactor = adjustmentFactorRepository.findById(paymentHistory.getUserId())
                .orElse(new AdjustmentFactor("", 0.7f)).getAdjustmentFactor();

            return PaymentAdjustmentDto.builder()
                .userId(paymentHistory.getUserId())
                .adjustmentedPrice((int)(paymentHistory.getPrice() * adjustmentFactor))
                .adjustmentedAt(LocalDateTime.now())
                .build();
        };
    }

}
