package com.github.mrcybear.timer;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void delayValueIsActual() {
        long expectedDelay = 10L;
        Task task = new Task(() -> {
        }, expectedDelay, TimeUnit.MILLISECONDS);
        long actualDelay = task.getDelay(TimeUnit.MILLISECONDS);
        assertTrue(actualDelay >= 7L);
    }

    @Test
    public void delayValueChangesOverTime() {
        Task task = new Task(() -> {
        }, 10L, TimeUnit.MILLISECONDS);
        timeout(10L);
        long actualDelay = task.getDelay(TimeUnit.MILLISECONDS);
        assertTrue(actualDelay <= 0L);
    }

    @Test
    public void tasksAreComparable() {
        Task task1 = new Task(() -> {
        }, 200L, TimeUnit.MILLISECONDS);
        Task task2 = new Task(() -> {
        }, 100L, TimeUnit.MILLISECONDS);
        assertTrue(task1.compareTo(task2) >= 95L);
    }

    @Test
    public void taskComparisonResultsAreTheSameOverTime() {
        Task task1 = new Task(() -> {
        }, 200L, TimeUnit.MILLISECONDS);
        Task task2 = new Task(() -> {
        }, 100L, TimeUnit.MILLISECONDS);
        int firstComparison = task1.compareTo(task2);
        timeout(150L);
        int secondComparison = task1.compareTo(task2);
        assertEquals(firstComparison, secondComparison);
    }

    @Test
    public void zeroDelayTaskExpiresImmediately() {
        Task task = new Task(() -> {
        }, 0L, TimeUnit.MILLISECONDS);
        assertTrue(task.getDelay(TimeUnit.MILLISECONDS) <= 0L);
    }

    @Test
    public void negativeDelayTaskExpiresImmediately() {
        Task task = new Task(() -> {
        }, -1L, TimeUnit.MILLISECONDS);
        assertTrue(task.getDelay(TimeUnit.MILLISECONDS) <= 0L);
    }

    @Test(expected = NullPointerException.class)
    public void nullRunnableIsNotAllowedInConstructor() {
        new Task(null, 10L, TimeUnit.MILLISECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void nullTimeUnitIsNotAllowedInConstructor() {
        new Task(() -> {
        }, 10L, null);
    }

    @Test
    public void zeroDelayIsAllowedInConstructor() {
        new Task(() -> {
        }, 0L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void negativeDelayValueIsAllowedInConstructor() {
        new Task(() -> {
        }, -1L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void taskRunnableIsAccessible() {
        Runnable runnable = () -> {
        };
        Task task = new Task(runnable, 0L, TimeUnit.MILLISECONDS);
        assertSame(runnable, task.getRunnable());
    }

    private void timeout(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }
}
