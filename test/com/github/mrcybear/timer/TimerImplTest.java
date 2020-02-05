package com.github.mrcybear.timer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimerImplTest {

    private Timer timer;
    private MockTask taskMock;

    @Before
    public void setup() {
        timer = new TimerImpl();
        taskMock = new MockTask();
    }

    @Test
    public void singleTimerFiresOnce() {
        timer.schedule(taskMock, 100, TimeUnit.MILLISECONDS);
        taskMock.timeout(110).times(1);
    }

    @Test
    public void singleTimerDoesNotFirePrematurely() {
        timer.schedule(taskMock, 200, TimeUnit.MILLISECONDS);
        taskMock.timeout(195).times(0);
        taskMock.timeout(15).times(1);
    }

    @Test
    public void multipleTimersScheduledFromSingleThreadFireSequentially() {
        timer.schedule(taskMock, 100, TimeUnit.MILLISECONDS);
        timer.schedule(taskMock, 200, TimeUnit.MILLISECONDS);
        timer.schedule(taskMock, 300, TimeUnit.MILLISECONDS);
        timer.schedule(taskMock, 400, TimeUnit.MILLISECONDS);
        timer.schedule(taskMock, 500, TimeUnit.MILLISECONDS);

        taskMock.timeout(110).times(1);
        taskMock.timeout(110).times(2);
        taskMock.timeout(110).times(3);
        taskMock.timeout(110).times(4);
        taskMock.timeout(110).times(5);
    }

    @Test
    public void multipleTimersScheduledFromMultipleThreadFireSequentially() {

        final long baseDelay = 100; // 100ms

        MockTask[] mockTaskList = new MockTask[10];
        for (int i = 0; i < mockTaskList.length; i++) {
            mockTaskList[i] = new MockTask();
        }

        // Scheduling here a set of timers from multiple threads
        for (int i = 0; i < mockTaskList.length; i++) {
            int finalI = i;
            new Thread(() -> timer.schedule(mockTaskList[finalI], baseDelay * (finalI + 1), TimeUnit.MILLISECONDS)).start();
            mockTaskList[finalI].signalProcessingStarted();
        }

        // Waiting processing to finish
        try {
            Thread.sleep((mockTaskList.length + 1) * baseDelay);
        } catch (Exception ex) {
        }

        // We check that our solution will execute a runnable task
        // exactly once after the specified time interval has elapsed
        for (int i = 0; i < mockTaskList.length; i++) {
            mockTaskList[i].times(1);
            Assert.assertTrue(mockTaskList[i].getProcessingTime() <= (baseDelay * (i + 1) + 10));
        }
    }

    @Test
    public void timerWithoutDelayFiresInstantly() {
        timer.schedule(taskMock, 0, TimeUnit.MILLISECONDS);
        taskMock.timeout(5).times(1);
    }

    @Test
    public void timerWithNegativeDelayFiresInstantly() {
        timer.schedule(taskMock, -1, TimeUnit.MILLISECONDS);
        taskMock.timeout(5).times(1);
    }
}