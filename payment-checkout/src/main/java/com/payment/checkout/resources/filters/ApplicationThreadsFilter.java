package com.payment.checkout.resources.filters;

import spark.Spark;

public class ApplicationThreadsFilter {

    public static void registerBeforeFilter() {
        Spark.before((request, response) -> ApplicationThreads.increment());
    }

    public static void registerAfterAfterFilter() {
        Spark.afterAfter((request, response) -> ApplicationThreads.decrement());
    }
}