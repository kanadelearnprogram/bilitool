package org.example.processor;

import org.example.model.Video;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务管理器，用于管理视频处理任务队列
 */
public class TaskManager {
    // 任务队列
    private final BlockingQueue<VideoTask> taskQueue = new LinkedBlockingQueue<>();
    
    // 任务ID生成器
    private final AtomicInteger taskIdGenerator = new AtomicInteger(0);
    
    /**
     * 添加任务到队列
     * @param video 视频对象
     * @return 任务ID
     */
    public int addTask(Video video) {
        int taskId = taskIdGenerator.incrementAndGet();
        VideoTask task = new VideoTask(taskId, video);
        taskQueue.offer(task);
        return taskId;
    }
    
    /**
     * 从队列中获取任务
     * @return 视频任务
     * @throws InterruptedException 当线程被中断时抛出
     */
    public VideoTask takeTask() throws InterruptedException {
        return taskQueue.take();
    }
    
    /**
     * 获取队列大小
     * @return 队列中的任务数量
     */
    public int getQueueSize() {
        return taskQueue.size();
    }
}