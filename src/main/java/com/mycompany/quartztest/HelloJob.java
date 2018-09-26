/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.quartztest;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nikolay
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class HelloJob implements InterruptableJob {

    static final Logger logger = LoggerFactory.getLogger(HelloJob.class);

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        logger.info("Job running at {}", jec.getFireTime());
        logger.info("Will run again at {}", jec.getNextFireTime());
//        JobDataMap jdm = jec.getJobDetail().getJobDataMap();
//        if (jdm != null) {
//            logger.info("With map key: {}", jdm.getString("key"));
//        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        logger.info("Job interrupted");
    }
}
