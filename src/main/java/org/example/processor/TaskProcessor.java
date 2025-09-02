package org.example.processor;

import org.example.model.Subtitle;
import org.example.model.Video;
import org.example.service.VideoService;
import org.example.utils.AiUtil;
import org.example.utils.FileUtils;
import org.example.utils.ParseSubtitle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 任务处理器，用于处理视频任务
 */
public class TaskProcessor {
    private final TaskManager taskManager;
    private final VideoService videoService;
    private final ExecutorService executorService;
    
    public TaskProcessor(TaskManager taskManager, VideoService videoService, int threadPoolSize) {
        this.taskManager = taskManager;
        this.videoService = videoService;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }
    
    /**
     * 开始处理任务
     */
    public void startProcessing() {
        // 提交任务处理线程
        executorService.submit(this::processTasks);
    }
    
    /**
     * 处理任务队列中的任务
     */
    private void processTasks() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 从任务队列中获取任务
                VideoTask task = taskManager.takeTask();
                
                // 更新任务状态
                task.setStatus(TaskStatus.PROCESSING);
                
                // 处理任务
                processVideoTask(task);
                
                // 更新任务状态为完成
                task.setStatus(TaskStatus.COMPLETED);
            } catch (InterruptedException e) {
                // 线程被中断，退出循环
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // 处理任务时发生错误
                System.err.println("处理任务时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 处理单个视频任务
     * @param task 视频任务
     */
    private void processVideoTask(VideoTask task) {
        Video video = task.getVideo();
        String bvid = video.getBvid();
        
        System.out.println("开始处理视频: " + bvid);
        
        // 处理视频的每个分P
        for (String cid : video.getCidMap().keySet()) {
            String partTitle = video.getCidMap().get(cid);
            System.out.println("处理视频 " + bvid + " 的分P " + partTitle + " (CID: " + cid + ")");
            
            try {
                // 获取字幕信息
                String subtitleInfo = ParseSubtitle.getSubtitleInfo(bvid, cid);
                task.incrementCompletedSteps();
                
                // 下载字幕
                List<Subtitle> subtitles = ParseSubtitle.downloadSubtitles(subtitleInfo, partTitle);

                Subtitle subtitle = subtitles.get(0);
                subtitles.stream().forEach(subtitle1 ->
                        FileUtils.saveSubtitleToFileAsync(subtitle1));

                String content = AiUtil.AISumarize(subtitle);
                String title = "AISummarize"+subtitle.getTitle();

                FileUtils.saveStrToFileAsync(title,content);

                task.incrementCompletedSteps();
                
                System.out.println("视频 " + bvid + " 的分P " + partTitle + " 处理完成");
            } catch (Exception e) {
                System.err.println("处理视频 " + bvid + " 的分P " + partTitle + " 时发生错误: " + e.getMessage());
                task.setStatus(TaskStatus.FAILED);
                throw e;
            }
        }
        
        System.out.println("视频 " + bvid + " 处理完成");
    }
    
    /**
     * 关闭任务处理器
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}