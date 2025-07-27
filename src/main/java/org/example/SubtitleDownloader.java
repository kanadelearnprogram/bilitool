package org.example;

import org.example.config.Config;
import org.example.model.Video;
import org.example.utils.ParseVideo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.config.Config.loadConfig;
import static org.example.utils.ParseBV.extractBVIdFromUrl;
import static org.example.utils.ParseSubtitle.*;
import static org.example.utils.ParseVideo.getVideoInfo;

/**
 * 字幕下载器，用于从 B 站下载视频字幕。
 */

// https://api.bilibili.com/x/web-interface/view?bvid=B
public class SubtitleDownloader {

    /**
     * 主方法，程序入口。
     * @param args 命令行参数，bvid
     */
    public static void main(String[] args) {
        loadConfig();
        if (args.length < 2) {
            System.out.println("请提供 bvid 参数");
            System.out.println("bvid BVxxxxx ");
            return;
        }
        // config
        // bvid  aid title BV1oA411c7Eh BV13TK6zaEcG BV1oouqz3E5K
        String mode = args[0];

        String bvid = "";
        switch (mode) {
            case "bvid":
                bvid = extractBVIdFromUrl(args[1]);
                break;
                // 添加修改模式config.json
        }

        if (bvid == null || bvid.isEmpty()) {
            System.out.println("bvid cannot be empty");
            return;
        }

        Video video = ParseVideo.fromJson(getVideoInfo(bvid));

        try {
            // 创建固定大小的线程池，避免创建过多线程
            ExecutorService executor = Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors());

            // 存储所有的CompletableFuture任务
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (String cid : video.cidMap.keySet()) {
                System.out.println("BV->" + bvid + "\t" + "CID->" + cid);
                // 在lambda表达式中需要final变量
                final String finalBvid = bvid;
                final String currentCid = cid;
                final String partTitle = video.cidMap.get(currentCid);

                // 异步处理每个分P的字幕下载
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        String subtitleInfo = getSubtitleInfo(finalBvid, currentCid);
                        downloadSubtitlesAsync(subtitleInfo, partTitle);
                    } catch (Exception e) {
                        System.err.println("下载字幕时出错 (CID: " + currentCid + "): " + e.getMessage());
                    }
                }, executor);

                futures.add(future);
            }

            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // 关闭线程池
            executor.shutdown();

            System.out.println("所有字幕下载完成");
        } catch (Exception e) {
            System.err.println("下载字幕时出错: " + e.getMessage());
        }
    }

}