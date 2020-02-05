package com.github.mrcybear.timer;

public class TaskExecutor {

    private final TaskQueue taskQueue = new TaskQueue();

    public TaskExecutor(int nThreads) {
        for (int i = 0; i < nThreads; i++) {
            new Thread(new TaskWorker()).start();
        }
    }

    public void schedule(Task task) {
        taskQueue.putTask(task);
    }

    private final class TaskWorker implements Runnable {

        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            for (; ; ) {
                try {
                    Task nextTask = taskQueue.getTask();
                    nextTask.getRunnable().run();
                } catch (InterruptedException ex) {
                }
            }
        }
    }
}
