package com.payment.checkout;

import com.payment.checkout.resources.filters.ApplicationThreads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;

public class PaymentCheckoutShutdown extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PaymentCheckoutShutdown.class);
    private static final int DELAY_TO_OUTPUT_STATUS = 1000;
    private static final Integer SUCCESS_CODE = 0;

    @Override
    public void run() {
        logger.info("Shutdown received. Starting stop gracefully...");
        PaymentCheckout.getInstance().setShutdownSignalReceived();
        try {
            while(ApplicationThreads.areThereActiveThreads()) {
                logger.info("Threads executing at Spark Server: " + ApplicationThreads.getCurrentThreadsRunning());
                Thread.sleep(DELAY_TO_OUTPUT_STATUS);
            }
            logger.info("All threads are finished! Finalizing...");                        
        } catch (InterruptedException e) {
            logger.error("Error occurred while waiting to finish threads: ", e);
        }
        Spark.stop();
        Runtime.getRuntime().halt(SUCCESS_CODE);
    }
}