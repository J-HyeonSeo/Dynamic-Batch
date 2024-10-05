package com.jhsfully.dynamicbatch.web;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job dayAdjustmentJob;

    public BatchController(
        JobLauncher jobLauncher,
        Job dayAdjustmentJob
    ) {
        this.jobLauncher = jobLauncher;
        this.dayAdjustmentJob = dayAdjustmentJob;
    }

    @PostMapping("/trigger/{inputDate}")
    public ResponseEntity<?> triggerAdjustmentJob(@PathVariable String inputDate) {

        JobParameters jobParameter = new JobParametersBuilder()
            .addString("inputDate", inputDate)
                .toJobParameters();

        try {
            JobExecution runningExecution = jobLauncher.run(dayAdjustmentJob, jobParameter);
            return ResponseEntity.ok(runningExecution.getJobId());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
