package com.github.mrcybear.timer;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TaskQueueTest {

    private TaskQueue queue;

    @Before
    public void setup() {
        queue = new TaskQueue();
    }

    @Test
    public void expiredTaskIsAvailableImmediately() throws InterruptedException {
        Task expectedTask = createAndPutTask(-1L);
        ExtractedTask extractedTask = extractTask();
        assertSame(expectedTask, extractedTask.task);
        assertTrue(extractedTask.timeSpent <= 5L);
    }

    @Test
    public void taskWithoutDelayIsAvailableImmediately() throws InterruptedException {
        Task expectedTask = createAndPutTask(0L);
        ExtractedTask extractedTask = extractTask();
        assertSame(expectedTask, extractedTask.task);
        assertTrue(extractedTask.timeSpent <= 5L);
    }

    @Test
    public void taskIsAvailableWhenDelayExpires() throws InterruptedException {
        Task expectedTask = createAndPutTask(100L);
        ExtractedTask extractedTask = extractTask(); // method returns after ~100ms
        assertSame(expectedTask, extractedTask.task);
        assertTrue(extractedTask.timeSpent >= 95L);
    }

    @Test
    public void multipleTaskAreAvailableAccordingTheirDelays() throws InterruptedException {
        Task[] incomingTasks = new Task[5];
        for (int i = 0; i < incomingTasks.length; i++) {
            incomingTasks[i] = createAndPutTask(500 - 100 * i); // 500ms, 400, ... 100
        }

        ExtractedTask[] extractedTasks = new ExtractedTask[incomingTasks.length];
        for (int i = 0; i < incomingTasks.length; i++) {
            extractedTasks[i] = extractTask();
        }

        for (int i = 0; i < incomingTasks.length; i++) {
            // We should get 500 ms delay task as the last expired task, but 100 ms delay task as the first expired
            assertSame(incomingTasks[incomingTasks.length - i - 1], extractedTasks[i].task);
            // We get first expired task (the 100ms delay task) after ~100ms (when delay expires)
            // Second expired task is the 200 ms delay task. We get it after the next ~100ms and so on.
            assertTrue(extractedTasks[i].timeSpent <= 110L);
        }
    }

    private Task createAndPutTask(long delayMillis) {
        Task task = new Task(() -> {
        }, delayMillis, TimeUnit.MILLISECONDS);
        queue.putTask(task);
        return task;
    }

    private ExtractedTask extractTask() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Task task = queue.getTask();
        long finishTime = System.currentTimeMillis();
        return new ExtractedTask(task, finishTime - startTime);
    }

    static class ExtractedTask {
        final long timeSpent;
        final Task task;

        ExtractedTask(Task task, long timeSpent) {
            this.task = task;
            this.timeSpent = timeSpent;
        }
    }
}
