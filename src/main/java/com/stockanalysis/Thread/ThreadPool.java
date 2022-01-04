package com.stockanalysis.Thread;

import java.util.concurrent.*;

public class ThreadPool {


    public static ThreadPoolExecutor newThreadPool() {

        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(512);
        RejectedExecutionHandler policy = new ThreadPoolExecutor.DiscardPolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.SECONDS, queue, policy);
        return threadPoolExecutor;
    }
}

