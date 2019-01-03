package com.payment.checkout;

import com.payment.checkout.database.ConnectionPool;
import com.payment.checkout.resources.filters.ApplicationThreads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;

public class PaymentCheckoutShutdown extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PaymentCheckoutShutdown.class);
    private static final int DELAY_TO_FINISH_THREADS = 1000;
    private static final int SUCCESS_CODE = 0;
    private static final int FAIL_CODE = 1;

    @Override
    public void run() {
        int code = SUCCESS_CODE;
        logger.info("Shutdown received. Starting stop gracefully...");
        PaymentCheckout.getInstance().setShutdownSignalReceived();
        try {
            while(ApplicationThreads.areThereActiveThreads()) {
                logger.info("Threads executing at Spark Server: " + ApplicationThreads.getCurrentThreadsRunning());
                Thread.sleep(DELAY_TO_FINISH_THREADS);
            }
            logger.info("All threads are finished! Finalizing...");            
            ConnectionPool.getInstance().close();            
            Spark.stop();
        } catch (Exception e) {
            code = FAIL_CODE;
            logger.error("Error occurred while stopping the application: ", e);
        } finally {
            Runtime.getRuntime().halt(code);
        }
    }
}