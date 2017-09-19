/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.quartztest;

import static com.mycompany.quartztest.Main.RESOURCES_PATH;
import java.io.FileReader;
import java.util.Properties;
import org.quartz.Job;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import static org.quartz.JobKey.jobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nikolay
 */
public class QuartzScheduler {

    static final Logger logger = LoggerFactory.getLogger(QuartzScheduler.class);
    
    public static void allocateJobToScheduler(
            Scheduler scheduler, Class<? extends Job> clazz, 
            String nameId, int intervalInSeconds, Object... objects) 
    throws SchedulerException {
        
        JobDetail job = newJob(clazz)
                .withIdentity(jobKey(nameId))
                .build();

        // Trigger the job to run now, and then every 40 seconds
        Trigger trigger = newTrigger()
                .withIdentity(triggerKey(nameId))
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                .build();
        
        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger);
    }

    public static Scheduler getScheduler() throws SchedulerException {

        SchedulerFactory schedFact;
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(RESOURCES_PATH + "/quartz.properties"));
            properties.put("org.quartz.dataSource.myDS.user", "SA");
            properties.put("org.quartz.dataSource.myDS.password", "");
            logger.info("Initializing Quartz scheduler with JDBCStore");
            schedFact = new org.quartz.impl.StdSchedulerFactory(properties);
        } catch (Throwable ex) {
            logger.error("Failed reading properties file", ex);
            logger.warn("Initializing Quartz scheduler with default behaviour");
            schedFact = new org.quartz.impl.StdSchedulerFactory();
        }

        Scheduler sched = schedFact.getScheduler();
        return sched;
    }

    public static void shutdownScheduler(Scheduler sched)
            throws SchedulerException {
        if (sched != null) {
            sched.shutdown();
        }
    }

    public static void shutdownScheduler(Scheduler sched, boolean waitTillShutdown)
            throws SchedulerException {
        if (sched != null) {
            sched.shutdown(waitTillShutdown);
        }
    }
}
