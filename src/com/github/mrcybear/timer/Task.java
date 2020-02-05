package com.github.mrcybear.timer;

import java.util.concurrent.TimeUnit;

public class Task implements Comparable<Task> {

    /*
     * We are defining a startTime – this is a time when the element
     * should be consumed from the TaskQueue instance. Next, we need
     * to implement the getDelay() method – it should return the remaining delay
     * associated with this object in the given time unit.
     */
    private final long startTime;

    private final Runnable runnable;

    public Task(Runnable runnable, long delay, TimeUnit timeUnit) {
        if (runnable == null) {
            throw new NullPointerException("runnable is null");
        }
        if (timeUnit == null) {
            throw new NullPointerException("timeUnit is null");
        }
        this.runnable = runnable;
        this.startTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, timeUnit);
    }

    /*
     * When the consumer tries to take an element from the queue,
     * the TaskQueue will execute getDelay() to find out if that
     * element is allowed to be returned from the queue. If the
     * getDelay() method will return zero or a negative number,
     * it means that it could be retrieved from the queue.
     */
    public long getDelay(TimeUnit target) {
        long diff = startTime - System.currentTimeMillis();
        return target.convert(diff, TimeUnit.MILLISECONDS);
    }

    public Runnable getRunnable() {
        return runnable;
    }

    /*
     * We also need to implement the compareTo() method, because
     * the elements in the TaskQueue will be sorted according to
     * the expiration time. The item that will expire first is kept
     * at the head of the queue and the element with the highest
     * expiration time is kept at the tail of the queue:
     */
    @Override
    public int compareTo(Task anotherTask) {
        return safeLongToInt(this.startTime - anotherTask.startTime);
    }

    private int safeLongToInt(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }
}
