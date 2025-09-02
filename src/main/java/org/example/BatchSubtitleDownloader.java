package org.example;

import org.example.config.Config;
import org.example.processor.BatchProcessingService;
import org.example.service.VideoService;
import org.example.utils.ParseBV;

import java.util.ArrayList;
import java.util.List;

import static org.example.config.Config.loadConfig;

/**
 * 批量字幕下载器，用于批量下载B站视频字幕
 */
public class BatchSubtitleDownloader {
    
    /**
     * 主方法，程序入口。
     * @param args 命令行参数，bvid列表
     */
    public static void main(String[] args) {
        // 加载配置
        loadConfig();
        
        if (args.length < 1) {
            System.out.println("请提供至少一个 bvid 参数");
            System.out.println("示例: java -jar bilitool.jar BVxxxxx BVyyyyy BVzzzzz");
            return;
        }
        
        // 创建视频服务
        VideoService videoService = new VideoService();
        
        // 创建批量处理服务
        BatchProcessingService batchProcessingService = new BatchProcessingService(videoService, 5);
        
        // 解析命令行参数中的BVID
        List<String> bvids = new ArrayList<>();
        for (String arg : args) {
            String bvid = ParseBV.extractBVIdFromUrl(arg);
            if (bvid != null && !bvid.isEmpty()) {
                bvids.add(bvid);
            }
        }
        
        if (bvids.isEmpty()) {
            System.out.println("未提供有效的 bvid 参数");
            return;
        }
        
        // 添加视频到处理队列
        batchProcessingService.addVideosToQueue(bvids);
        
        // 开始批量处理
        batchProcessingService.startBatchProcessing();
        
        try {
            // 等待所有任务完成
            batchProcessingService.waitForCompletion();
            
            // 输出处理结果
            System.out.println("批量处理完成");
            System.out.println("成功处理任务数: " + batchProcessingService.getCompletedTasks());
            System.out.println("失败任务数: " + batchProcessingService.getFailedTasks());
        } catch (InterruptedException e) {
            System.err.println("等待任务完成时被中断");
            Thread.currentThread().interrupt();
        } finally {
            // 关闭批量处理服务
            batchProcessingService.shutdown();
        }
    }
}