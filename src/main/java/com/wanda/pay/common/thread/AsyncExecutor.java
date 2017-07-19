package com.wanda.pay.common.thread;

import com.wanda.pay.common.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Description 统一线程池工具类，线程池大小默认为 CPU核数*2
 * @Author liangrun
 * @Create 2016-12-29 15:59
 * @Version 1.0
 */
@Slf4j
public class AsyncExecutor {
    private static ExecutorService executorService;

    private static volatile AsyncExecutor instance;

    private AsyncExecutor() {
        executorService = Executors.newFixedThreadPool(Config.DEFAULT_THREAD_POOL_SIZE);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                executorService.shutdown();
                log.info("Shutdown thread pool finished");
            }
        });
    }

    public static AsyncExecutor getInstance() {
        if (instance == null) {
            synchronized (AsyncExecutor.class) {
                if (instance == null) {
                    instance = new AsyncExecutor();
                }
            }
        }
        return instance;
    }

    public void execute(Runnable command) {
        executorService.execute(command);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return executorService.submit(task, result);
    }

    public Future<?> submit(Runnable task) {
        return executorService.submit(task);
    }
}
