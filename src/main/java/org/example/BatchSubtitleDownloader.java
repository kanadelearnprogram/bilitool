package org.example;

import org.example.config.Config;
import org.example.model.Subtitle;
import org.example.model.Video;
import org.example.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
        
        // 使用虚拟线程处理批量任务
        try (ExecutorService executor = AsyncUtil.createVirtualThreadExecutor()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (String bvid : bvids) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processVideo(bvid), executor);
                futures.add(future);
            }
            
            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            System.out.println("批量处理完成");
        } catch (Exception e) {
            System.err.println("批量处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 处理单个视频
     * @param bvid 视频BVID
     */
    private static void processVideo(String bvid) {
        try {
            System.out.println("开始处理视频: " + bvid);
            
            Video video = ParseVideo.fromJson(ParseVideo.getVideoInfo(bvid));
            ExecutorService executor = AsyncUtil.createVirtualThreadExecutor();
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            // 处理视频的每个分P
            for (String cid : video.getCidMap().keySet()) {
                String partTitle = video.getCidMap().get(cid);
                System.out.println("处理视频 " + bvid + " 的分P " + partTitle + " (CID: " + cid + ")");
                
                try {
                    // 获取字幕信息
                    String subtitleInfo = ParseSubtitle.getSubtitleInfo(bvid, cid);
                    
                    // 下载字幕
                    List<Subtitle> subtitles = ParseSubtitle.downloadSubtitles(subtitleInfo, partTitle);
                    
                    if (subtitles != null) {
                        for (Subtitle subtitle : subtitles) {
                            FileUtils.saveSubtitleToFile(subtitle);
                            System.out.println("save subtitle ->" + subtitle.getTitle());
                            // 异步进行AI总结，不等待完成
                            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                                try {
                                    String summary = AIUtil.summarize(subtitle);
                                    if (summary != null) {
                                        FileUtils.saveStrToFile(subtitle.getTitle(), summary, "AISummarize");
                                        System.out.println("save AISummarize" + subtitle.getTitle());
                                    }
                                } catch (Exception e) {
                                    System.err.println("AI总结失败: " + e.getMessage());
                                }
                            }, executor);
                            futures.add(future);
                        }
                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                    }
                    
                    System.out.println("视频 " + bvid + " 的分P " + partTitle + " 处理完成");
                } catch (Exception e) {
                    System.err.println("处理视频 " + bvid + " 的分P " + partTitle + " 时发生错误: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("视频 " + bvid + " 处理完成");
        } catch (Exception e) {
            System.err.println("处理视频 " + bvid + " 时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}