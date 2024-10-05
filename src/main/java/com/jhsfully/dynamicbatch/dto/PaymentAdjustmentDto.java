package com.jhsfully.dynamicbatch.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentAdjustmentDto {

    private String userId;
    private int adjustmentedPrice;
    private LocalDateTime adjustmentedAt;

}
