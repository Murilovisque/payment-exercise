package com.payment.checkout.resources.filters;

import java.util.UUID;

import org.slf4j.MDC;

import spark.Request;
import spark.Spark;

public class MDCLoggerFilter {

    private static final String LOG_PLACEHOLDER_REQUEST_ID = "PaymentCheckout-RequestId";
    private static final String LOG_IDENTIFIER_PREFIX = " [Request: ";
    private static final String LOG_IDENTIFIER_SUFFIX = "]";

    public static void registerBeforeFilter() {
        Spark.before((request, response) -> putMDCIdentifier(request));
    }

    private static void putMDCIdentifier(Request request) {
        String requestId = new StringBuilder(LOG_IDENTIFIER_PREFIX).append(UUID.randomUUID().toString()).append(LOG_IDENTIFIER_SUFFIX).toString();
        MDC.put(LOG_PLACEHOLDER_REQUEST_ID, requestId);
    }

    public static void registerAfterAfterFilter() {
        Spark.afterAfter((request, response) -> MDC.remove(LOG_PLACEHOLDER_REQUEST_ID));
    }
}