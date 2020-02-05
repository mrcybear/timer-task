package com.github.mrcybear.timer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MockTask implements Runnable {
    private final AtomicLong numberOfInvocations = new AtomicLong(0L);
    private long processingStarted;
    private long processingFinished;

    @Override
    public void run() {
        numberOfInvocations.getAndIncrement();
        processingFinished = System.currentTimeMillis();
    }

    void signalProcessingStarted() {
        processingStarted = System.currentTimeMillis();
    }

    long getProcessingTime() {
        return processingFinished - processingStarted;
    }

    void times(long times) {
        assertThat("Number of invocations is incorrect", numberOfInvocations.get(), is(times));
    }

    MockTask timeout(long delay) {
        return timeout(delay, TimeUnit.MILLISECONDS);
    }

    MockTask timeout(long delay, TimeUnit unit) {
        final long timeout = TimeUnit.MILLISECONDS.convert(delay, unit);
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
        return this;
    }
}