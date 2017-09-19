/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.quartztest;

import java.sql.SQLException;
import java.util.Scanner;
import static org.quartz.JobKey.jobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nikolay
 */
public class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static final String RESOURCES_PATH = "./src/main/resources";

    public static void main(String[] args) {

        try {
            ConnectionManager.initQuartzSqlTables();

            Scheduler scheduler = QuartzScheduler.getScheduler();

            QuartzScheduler.allocateJobToScheduler(
                    scheduler, HelloJob.class, "job1", 5);
            
            QuartzScheduler.allocateJobToScheduler(
                    scheduler, HelloJob.class, "job2", 10);
            
            scheduler.start();
            
            scheduler.interrupt(jobKey("job1"));
            

            logger.info("Press enter to finish");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();

            logger.info("Shutting down quartz");
            QuartzScheduler.shutdownScheduler(scheduler, true);
            logger.info("DONE!!");

        } catch (SchedulerException ex) {
            logger.error("SchedulerException", ex);
        } catch (SQLException ex) {
            logger.error("SQLException", ex);
        }
    }
}
