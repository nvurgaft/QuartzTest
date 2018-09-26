/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.quartztest;

import static com.mycompany.quartztest.FileSystem.getQuartzProperties;
import java.io.InputStream;
import java.nio.file.Files;
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
    
    private static Scheduler scheduler;
    
    public static Scheduler getScheduler() throws SchedulerException {
        
        if (scheduler == null) {
            synchronized (QuartzScheduler.class) {
                if (scheduler == null) {
                    SchedulerFactory schedFact;
                    try {
                        Properties properties = new Properties();
                        try (InputStream is = Files.newInputStream(getQuartzProperties())) {
                            properties.load(is);
                        }
//                        properties.load(new FileReader(RESOURCES_PATH + "/quartz.properties"));
                        properties.put("org.quartz.dataSource.myDS.user", "SA");
                        properties.put("org.quartz.dataSource.myDS.password", "");
                        logger.info("Initializing Quartz scheduler with JDBCStore");
                        schedFact = new org.quartz.impl.StdSchedulerFactory(properties);
                    } catch (Throwable ex) {
                        logger.error("Failed reading properties file", ex);
                        logger.warn("Initializing Quartz scheduler with default behaviour");
                        schedFact = new org.quartz.impl.StdSchedulerFactory();
                    }
                    
                    scheduler = schedFact.getScheduler();
                    if (scheduler == null) {
                        throw new RuntimeException("Scheduler is null");
                    }
                }
            }
        }
        return scheduler;
    }
    
    public static void scheduleJob(Class<? extends Job> clazz,
            String nameId, int intervalInSeconds, boolean isDurable)
            throws SchedulerException {
        
        JobDetail job = newJob(clazz)
                .withIdentity(jobKey(nameId))
                .storeDurably(isDurable)
                .build();
        
        Trigger trigger = newTrigger()
                .withIdentity(triggerKey(nameId))
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                .build();
        
        if (!getScheduler().checkExists(jobKey(nameId))) {
            
            getScheduler().scheduleJob(job, trigger);
        } else {
            logger.info("Job '{}' already exists", nameId);
        }
    }
    
    public static void resetScheduler() throws SchedulerException {
        getScheduler().clear();
    }
    
    public static void shutdownScheduler()
            throws SchedulerException {
        getScheduler().shutdown();
    }
    
    public static void shutdownScheduler(boolean waitTillShutdown)
            throws SchedulerException {
        getScheduler().shutdown(waitTillShutdown);
    }
}
