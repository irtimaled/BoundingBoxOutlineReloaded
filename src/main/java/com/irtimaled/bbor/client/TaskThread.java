package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.providers.BiomeBorderProvider;

import java.util.concurrent.locks.LockSupport;

public class TaskThread extends Thread {

    public static void init() {
    }

    public static final TaskThread INSTANCE = new TaskThread();

    private TaskThread() {
        this.setName("BBOR Task Thread");
        this.setDaemon(true);
        this.setPriority(Thread.NORM_PRIORITY - 1);
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                boolean hasTask = false;
                hasTask |= BiomeBorderProvider.runQueuedTasks();
                if (!hasTask) {
                    LockSupport.parkNanos(100_000_000L);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
