package com.github.mrcybear.timer;

import java.util.concurrent.TimeUnit;

public interface Timer {

    void schedule(Runnable command, long delay, TimeUnit unit);
}
