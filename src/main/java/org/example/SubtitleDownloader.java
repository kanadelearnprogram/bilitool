package org.example;

import org.example.config.Config;
import org.example.model.Subtitle;
import org.example.model.Video;
import org.example.utils.AsyncUtil;
import org.example.utils.ParseVideo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.example.config.Config.loadConfig;
import static org.example.utils.ParseBV.extractBVIdFromUrl;
import static org.example.utils.ParseSubtitle.downloadSubtitles;
import static org.example.utils.ParseSubtitle.getSubtitleInfo;
import static org.example.utils.ParseVideo.getVideoInfo;

/**
 * 字幕下载器，用于从 B 站下载视频字幕。
 */
public class SubtitleDownloader {

    /**
     * 主方法，程序入口。
     * @param args 命令行参数，bvid
     */
    /*public static void main(String[] args) {
        loadConfig();
        if (args.length < 2) {
            System.out.println("请提供 bvid 参数");
            System.out.println("bvid BVxxxxx ");
            return;
        }
        
        String mode = args[0];
        String bvid = "";
        
        switch (mode) {
            case "bvid":
                bvid = extractBVIdFromUrl(args[1]);
                break;
        }

        if (bvid == null || bvid.isEmpty()) {
            System.out.println("bvid cannot be empty");
            return;
        }

        Video video = ParseVideo.fromJson(getVideoInfo(bvid));

        try (ExecutorService executor = AsyncUtil.createVirtualThreadExecutor()) {
            // 使用虚拟线程执行所有任务
            for (String cid : video.getCidMap().keySet()) {
                System.out.println("BV->" + bvid + "\t" + "CID->" + cid);
                String finalBvid = bvid;
                String currentCid = cid;
                String partTitle = video.getCidMap().get(currentCid);

                // 提交任务到虚拟线程执行器
                executor.submit(() -> {
                    try {
                        // 获取字幕信息
                        String subtitleInfo = getSubtitleInfo(finalBvid, currentCid);
                        // 下载字幕
                        List<Subtitle> subtitles = downloadSubtitles(subtitleInfo, partTitle);
                        
                        // 处理每个字幕（保存和AI总结）
                        if (subtitles != null) {
                            for (Subtitle subtitle : subtitles) {
                                // 保存字幕文件
                                org.example.utils.FileUtils.saveSubtitleToFile(subtitle);
                                
                                // 异步进行AI总结，不等待完成
                                CompletableFuture.runAsync(() -> {
                                    try {
                                        String summary = org.example.utils.AIUtil.summarize(subtitle);
                                        if (summary != null) {
                                            org.example.utils.FileUtils.saveStrToFile(
                                                subtitle.getTitle(), summary, "ai_summarize");
                                        }
                                    } catch (Exception e) {
                                        System.err.println("AI总结失败: " + e.getMessage());
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("下载失败 (CID: " + currentCid + "): " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            // 等待所有任务完成
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                System.err.println("字幕下载任务超时");
            }

            System.out.println("所有字幕下载完成");
        } catch (Exception e) {
            System.err.println("下载字幕时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }*/
}