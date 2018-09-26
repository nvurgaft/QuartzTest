/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.quartztest;

import static com.mycompany.quartztest.QuartzScheduler.getScheduler;
import java.sql.SQLException;
import java.util.Scanner;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nikolay
 */
public class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            
            ConnectionManager.init();

            QuartzScheduler.scheduleJob(HelloJob.class, "job1", 60, false);
//            QuartzScheduler.scheduleJob(HelloJob.class, "job2", 10, false);
            getScheduler().start();
            
            logger.info("Press enter to continue");
            new Scanner(System.in).nextLine();

            logger.info("Currently executing jobs");
            for (JobExecutionContext jec : getScheduler().getCurrentlyExecutingJobs()) {
                logger.info("Running job: {}", jec.getJobDetail().toString());
            }

//            scheduler.interrupt(jobKey("job1"));
            logger.info("Press enter to finish");
            new Scanner(System.in).nextLine();

            logger.info("Shutting down quartz");
            QuartzScheduler.shutdownScheduler(true);

            logger.info("Shutting down database");
            ConnectionManager.shutdown();

            logger.info("DONE!!");

        } catch (SchedulerException ex) {
            logger.error("SchedulerException", ex);
        } catch (SQLException ex) {
            logger.error("SQLException", ex);
        }
    }
}
