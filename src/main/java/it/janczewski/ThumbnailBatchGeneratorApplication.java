package it.janczewski;

import it.janczewski.batchProcessing.utils.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class ThumbnailBatchGeneratorApplication {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String imageFolderPatch = "e:/images";

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    protected Tasklet tasklet() {
        return (stepContribution, chunkContext) -> {
            ImageUtils.procesImages(imageFolderPatch);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job job() {
        return this.jobs.get("job").start(step1()).build();
    }

    @Bean
    public Step step1() {
        return this.steps.get("step1").tasklet(tasklet()).build();
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(ThumbnailBatchGeneratorApplication.class, args)));
    }
}
