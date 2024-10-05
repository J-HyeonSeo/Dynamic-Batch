package com.jhsfully.dynamicbatch.repository;

import com.jhsfully.dynamicbatch.entity.AdjustmentFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdjustmentFactorRepository extends JpaRepository<AdjustmentFactor, String> {

}
