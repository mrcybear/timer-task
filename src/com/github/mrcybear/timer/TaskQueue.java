package com.github.mrcybear.timer;

import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class TaskQueue {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition newTaskIsAvailable = lock.newCondition();
    /*
     * We use an unbounded PriorityQueue instance to transfer and hold submitted tasks.
     * The Java implementation provides O(log(n)) time for the enque and dequeue
     * methods (offer, poll); constant time for the retrieval methods (peek).
     *
     * Using an unbounded queue will cause new scheduled tasks to wait in the queue when
     * all nThreads (task processing threads, as stated by TimerImpl class) are busy.
     * nThreads is a finite and constant value. Thus, no more than nThreads will ever be created.
     * This may be appropriate when each task is completely independent of others, so tasks
     * cannot affect each other's execution.
     * While this style of queuing can be useful in smoothing out bursts of requests
     * (as requested by the test task description at HackerRank), it admits the possibility of
     * unbounded work queue growth when commands continue to arrive on average faster than
     * they can be processed.
     *
     * A bounded queue helps prevent resource exhaustion when used with finite nThreads,
     * but can be more difficult to tune and control. Queue size and nThreads count
     * may be traded off for each other: Using large queues and small nThreads minimizes CPU usage,
     * OS resources, and context-switching overhead, but can lead to artificially low throughput.
     * If tasks frequently block (for example if they are I/O bound), a system may be able to schedule
     * a time for more threads than we otherwise allow. The use of small queues generally requires
     * larger nThreads, which keeps CPUs busier but may encounter unacceptable scheduling overhead,
     * which also decreases throughput.
     *
     * We can not increase nThreads value (task processing threads count), because context switching
     * overhead increases too. The optimal nThreads value is Runtime.getRuntime().availableProcessors() + 1)
     * as stated by TimerImpl class. So we have a finite worker pool.
     *
     * We required to provide a solution that will execute a runnable task exactly once after the specified time
     * interval (the delay time) has elapsed. So using a bounded queue is not a good idea because
     * of low throughput and high possibility of task timings violations.
     */
    private final PriorityQueue<Task> taskQueue;
    private Thread taskProcessingThread;

    public TaskQueue() {
        taskQueue = new PriorityQueue<>();
    }

    /*
     * Inserts the specified element into this queue.
     * As the queue is unbounded this method will never block.
     */
    public void putTask(Task newTask) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            taskQueue.offer(newTask);
            if (taskQueue.peek() == newTask) {
                taskProcessingThread = null;
                newTaskIsAvailable.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    /*
     * Retrieves and removes the head of this queue, waiting if necessary
     * until a task with an expired delay is available on this queue.
     *
     * For putTask() and getTask() we use the Leader-Follower pattern.
     * It has less contention, less passing of work between threads.
     * Coordinating thread communication/synchronization often requires
     * the use of locks etc that have a very negative impact on code
     * running on many processors.
     */
    public Task getTask() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (; ; ) {
                Task taskToBeProcessed = taskQueue.peek();
                if (taskToBeProcessed == null)
                    newTaskIsAvailable.await();
                else {
                    long taskDelay = taskToBeProcessed.getDelay(NANOSECONDS);
                    if (taskDelay <= 0L)
                        return taskQueue.poll();
                    if (taskProcessingThread != null)
                        newTaskIsAvailable.await();
                    else {
                        Thread thisThread = Thread.currentThread();
                        taskProcessingThread = thisThread;
                        try {
                            newTaskIsAvailable.awaitNanos(taskDelay);
                        } finally {
                            if (taskProcessingThread == thisThread)
                                taskProcessingThread = null;
                        }
                    }
                }
            }
        } finally {
            if (taskProcessingThread == null && taskQueue.peek() != null)
                newTaskIsAvailable.signal();
            lock.unlock();
        }
    }
}