package org.example.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步处理工具类，利用JDK 21的虚拟线程特性
 */
public class AsyncUtil {
    /**
     * 创建基于虚拟线程的执行器
     * 虚拟线程是JDK 21引入的轻量级线程，适合I/O密集型任务
     * 
     * @return 基于虚拟线程的执行器
     */
    public static ExecutorService createVirtualThreadExecutor( ) {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * 异步执行任务并返回CompletableFuture
     * 
     * @param task 要执行的任务
     * @param executor 执行器
     * @return CompletableFuture
     */
    public static CompletableFuture<Void> runAsync(Runnable task, ExecutorService executor) {
        return CompletableFuture.runAsync(task, executor);
    }

    /**
     * 组合多个CompletableFuture并等待全部完成
     * 
     * @param futures CompletableFuture数组
     * @return 组合后的CompletableFuture
     */
    @SafeVarargs
    public static CompletableFuture<Void> allOf(CompletableFuture<Void>... futures) {
        return CompletableFuture.allOf(futures);
    }
}