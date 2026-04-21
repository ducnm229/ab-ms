package com.ab.ms.order.config;

import feign.RetryableException;
import feign.Retryer;

// UNUSED: using Resilience4j retry instead
public class ExponentialBackoffRetryer implements Retryer {

    private final int maxAttempts;
    private final long initialInterval;
    private final long maxInterval;

    private int attempt;
    private long currentInterval;

    public ExponentialBackoffRetryer() {
        this(3, 200, 2000);
    }

    public ExponentialBackoffRetryer(int maxAttempts, long initialInterval, long maxInterval) {
        this.maxAttempts = maxAttempts;
        this.initialInterval = initialInterval;
        this.maxInterval = maxInterval;
        this.attempt = 1;
        this.currentInterval = initialInterval;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= maxAttempts) {
            throw e;
        }

        try {
            Thread.sleep(currentInterval);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw e;
        }

        currentInterval = Math.min(currentInterval * 2, maxInterval);
    }

    @Override
    public Retryer clone() {
        return new ExponentialBackoffRetryer(maxAttempts, initialInterval, maxInterval);
    }
}