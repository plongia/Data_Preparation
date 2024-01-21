package com.wiredbraincoffee.automate.schedule;


import com.wiredbraincoffee.automate.schedule.tasks.CSVIngesterTaskWithDelete;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IngesterJob implements Job {
    final static Logger logger = LoggerFactory.getLogger(IngesterJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ExecutorService executor = Executors.newCachedThreadPool();
        logger.info("Running job {}", context.getJobDetail().getKey());
        JobDataMap data = context.getMergedJobDataMap();
        String sourceFolder = data.getString("sourceFolder");
        String targetFile = data.getString("targetFile");
        try {
            Files.newDirectoryStream(Paths.get(sourceFolder))
                    .forEach(filePath -> {
                        try {
                            String type = Files.probeContentType(filePath);
                            if (type != null) {
                                IngesterTaskWithDelete task = new CSVIngesterTaskWithDelete(filePath.toAbsolutePath().toString(), targetFile);
                                if (task != null) executor.submit(task);
                            }
                        } catch (IOException e) {
                e.printStackTrace();
            }
        });
    } catch (IOException e) {
        throw new JobExecutionException(e);
    }
    Instant nextTrigger = context.getNextFireTime().toInstant();
        logger.info("Completed job {} - next run at {}", context.getJobDetail().getKey(), nextTrigger);
}
}
