package com.github.mrcybear.timer;

import java.util.concurrent.TimeUnit;

public class TimerImpl implements Timer {

    /*
     * If threads do not block each other and do not wait for I/O,
     * if processing time is the same for all scheduled tasks,
     * optimal nThreads value (how many processing threads should we start?)
     * is (Runtime.getRuntime().availableProcessors() + 1).
     *
     * If scheduled tasks spend time for I/O operations,
     * threads value should be increased for the ratio value = Total task processing time / I/O waiting time.
     * For example, we have a task that spends 50% of executing time for I/O wait,
     * so nThreads should be equal to (2 * Runtime.getRuntime().availableProcessors() + 1).
     */
    private final int nThreads = Runtime.getRuntime().availableProcessors() + 1;

    private final TaskExecutor taskExecutor = new TaskExecutor(nThreads);

    @Override
    public void schedule(Runnable command, long delay, TimeUnit unit) {
        taskExecutor.schedule(new Task(command, delay, unit));
    }
}
