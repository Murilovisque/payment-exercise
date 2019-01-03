package com.payment.checkout;

import java.beans.PropertyVetoException;

import com.payment.checkout.database.ConnectionPool;
import com.payment.checkout.exceptions.InitializationPaymenCheckoutException;
import com.payment.checkout.resources.BuyerResources;
import com.payment.checkout.resources.FormOfPaymentResources;
import com.payment.checkout.resources.PaymentResources;
import com.payment.checkout.resources.filters.ApplicationThreadsFilter;
import com.payment.checkout.resources.filters.MDCLoggerFilter;
import com.payment.checkout.resources.filters.ShutdownFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;

public class PaymentCheckout {

    private static final PaymentCheckout instance = new PaymentCheckout();
    private static final Logger logger = LoggerFactory.getLogger(PaymentCheckout.class);
    private boolean shutdownSignalReceived = false;

    public static void main(String[] args) throws Exception {
        PaymentCheckout.getInstance().start(); 
    }

    public void start() throws Exception {
        try {
            loadConfigs();
            buildConnectionPool();
            releaseHTTPResources();
            releaseHTTPFilters();
            loadShutdownHook();
        } catch (Exception ex) {
            logger.error("Project initialization problems", ex);
            throw ex;
        }
    }    

    private void loadConfigs() throws InitializationPaymenCheckoutException {
        PaymentCheckoutConfig.load();
    }

    public void buildConnectionPool() throws InitializationPaymenCheckoutException {
        ConnectionPool.getInstance().build();
        logger.info("Connection pool built");
    }

    public void releaseHTTPResources() {
        Spark.port(PaymentCheckoutConfig.getApplicationPort());
        PaymentResources.releaseHttpResources();
        FormOfPaymentResources.releaseHttpResources();
        BuyerResources.releaseHttpResources();
        logger.info("HTTP resources released");
    }

    private void releaseHTTPFilters() {
        ShutdownFilter.registerBeforeFilter();
        ApplicationThreadsFilter.registerBeforeFilter();
        MDCLoggerFilter.registerBeforeFilter();
        MDCLoggerFilter.registerAfterAfterFilter();
        ApplicationThreadsFilter.registerAfterAfterFilter();
    }

    private void loadShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new PaymentCheckoutShutdown());
    }

    public boolean isShutdownSignalReceived() {
        return shutdownSignalReceived;
    }

    void setShutdownSignalReceived() {
        shutdownSignalReceived = true;
    }

    public static PaymentCheckout getInstance() {
        return instance;
    }
}
