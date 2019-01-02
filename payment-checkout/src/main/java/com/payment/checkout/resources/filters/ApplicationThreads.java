package com.payment.checkout.resources.filters;

import java.util.concurrent.atomic.AtomicInteger;

public class ApplicationThreads {

    private static final AtomicInteger CURRENT_THREADS = new AtomicInteger(0);

    static void increment() {
        CURRENT_THREADS.incrementAndGet();
    }

    static void decrement() {
        CURRENT_THREADS.decrementAndGet();
    }

    public static boolean areThereActiveThreads() {
        return CURRENT_THREADS.get() > 0;
    }

    public static int getCurrentThreadsRunning() {
        return CURRENT_THREADS.get();
    }
}