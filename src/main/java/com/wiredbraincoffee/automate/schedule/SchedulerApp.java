package com.wiredbraincoffee.automate.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.quartz.DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class SchedulerApp {

    final static Logger logger = LoggerFactory.getLogger(SchedulerApp.class);

    public static void main(String[] args) {

        logger.info("*****************************************************");
        logger.info("Starting up daemon service for Global Sales Data Ingestion");

        Properties props = System.getProperties();
        int dayRunEveryMins;
        int nightRunEveryMins;
        String sourceFolder;
        String targetFolder;

        try {
            dayRunEveryMins = Integer.parseInt(props.getProperty("dayRunMins", "30"));
            nightRunEveryMins = Integer.parseInt(props.getProperty("nightRunMins", "180"));
            sourceFolder = props.getProperty("sourceFolder", "wiredbraincoffee/filedrop");
            targetFolder = props.getProperty("targetFolder", "wiredbraincoffee/datasink");
        } catch (NumberFormatException e) {
            logger.info("Configuration error: dayRunMins or nightRunMins are not valid numbers");
            System.exit(1);
            return;
        }

        StdSchedulerFactory sF = new StdSchedulerFactory();
        Scheduler scheduler = null;

        try {
            scheduler = sF.getScheduler();

            JobDetail job = newJob(IngesterJob.class).
                    withIdentity("ingest-job", "WiredBrainCoffeeGroup")
                    .usingJobData("sourceFolder", sourceFolder)
                    .usingJobData("targetFile", targetFolder + "/sales-global.dat")
                    .storeDurably()
                    .build();

            scheduler.addJob(job, false);

            logger.info("Scheduling job for {} to run every {} minutes during the day", targetFolder, dayRunEveryMins);

            Trigger triggerDay = newTrigger()
                    .withIdentity("ingest-trigger-day", "WiredBrainCoffeeGroup")
                    .startNow()
                    .withSchedule(dailyTimeIntervalSchedule()
                            .onMondayThroughFriday()
                            .withIntervalInMinutes(dayRunEveryMins)
                            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(7, 30))
                            .endingDailyAt(TimeOfDay.hourAndMinuteOfDay(7, 30))
                    )
                    .forJob(job)
                    .build();

            logger.info("Scheduling job for {} to run every {} minutes during the night", targetFolder, nightRunEveryMins);

            Trigger triggerNight = newTrigger()
                    .withIdentity("ingest-trigger-night", "WiredBrainCoffeeGroup")
                    .startNow()
                    .withSchedule(dailyTimeIntervalSchedule()
                            .onMondayThroughFriday()
                            .withIntervalInMinutes(nightRunEveryMins)
                            .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(0, 0))
                            .endingDailyAt(TimeOfDay.hourAndMinuteOfDay(6, 0))
                    )
                    .forJob(job)
                    .build();

            scheduler.scheduleJob(triggerDay);
            scheduler.scheduleJob(triggerNight);

            scheduler.start();

            logger.info("Scheduled job for {} every {} and {} minutes", targetFolder, triggerDay, triggerNight);

            final Scheduler schedulerAtShutdown = scheduler;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    logger.info("Shutting down daemon service for Global Sales Data Ingestion");
                    schedulerAtShutdown.shutdown(true);
                } catch (SchedulerException e) {
                    logger.error("Problem shutting the scheduler down", e);
                } finally {
                    logger.info("=======================================================");
                }
            }));

        } catch (SchedulerException e) {
            logger.error("Problem with the scheduler", e);
            if (scheduler != null) {
                try {
                    scheduler.shutdown();
                } catch (SchedulerException ignored) {
                }
            }
        }
    }
}
