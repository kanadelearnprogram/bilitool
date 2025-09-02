package org.example.processor;

import org.example.model.Video;
import org.example.service.VideoService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量处理服务，用于管理批量视频处理
 */
public class BatchProcessingService {
    private final TaskManager taskManager;
    private final TaskProcessor taskProcessor;
    private final VideoService videoService;
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private final AtomicInteger failedTasks = new AtomicInteger(0);
    
    public BatchProcessingService(VideoService videoService, int threadPoolSize) {
        this.videoService = videoService;
        this.taskManager = new TaskManager();
        this.taskProcessor = new TaskProcessor(taskManager, videoService, threadPoolSize);
    }
    
    /**
     * 添加视频到处理队列
     * @param bvid 视频BVID
     */
    public void addVideoToQueue(String bvid) {
        try {
            Video video = videoService.getVideoInfo(bvid);
            taskManager.addTask(video);
            System.out.println("已添加视频到处理队列: " + bvid);
        } catch (Exception e) {
            System.err.println("获取视频信息失败: " + bvid + ", 错误: " + e.getMessage());
            failedTasks.incrementAndGet();
        }
    }
    
    /**
     * 添加多个视频到处理队列
     * @param bvids 视频BVID列表
     */
    public void addVideosToQueue(List<String> bvids) {
        for (String bvid : bvids) {
            addVideoToQueue(bvid);
        }
    }
    
    /**
     * 开始批量处理
     */
    public void startBatchProcessing() {
        System.out.println("开始批量处理视频...");
        taskProcessor.startProcessing();
    }
    
    /**
     * 等待所有任务完成
     * @throws InterruptedException 当线程被中断时抛出
     */
    public void waitForCompletion() throws InterruptedException {
        // 简单实现：每秒检查一次队列是否为空
        while (taskManager.getQueueSize() > 0) {
            Thread.sleep(1000);
            System.out.println("剩余任务数量: " + taskManager.getQueueSize());
        }
        
        // 等待一段时间确保最后的任务处理完成
        Thread.sleep(5000);
    }
    
    /**
     * 关闭批量处理服务
     */
    public void shutdown() {
        taskProcessor.shutdown();
    }
    
    /**
     * 获取完成的任务数量
     * @return 完成的任务数量
     */
    public int getCompletedTasks() {
        return completedTasks.get();
    }
    
    /**
     * 获取失败的任务数量
     * @return 失败的任务数量
     */
    public int getFailedTasks() {
        return failedTasks.get();
    }
}