package com.jhsfully.dynamicbatch.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "adjustment_factor")
public class AdjustmentFactor {

    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name = "adjustment_factor")
    private float adjustmentFactor;

}
