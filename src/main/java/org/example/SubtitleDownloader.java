package org.example;

import cn.hutool.core.io.FileUtil;
import org.example.config.Config;
import org.example.model.Client;
import org.example.model.Video;
import org.example.utils.AsyncUtil;
import org.example.utils.ParseVideo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.example.config.Config.loadConfig;
import static org.example.utils.ParseBV.extractBVIdFromUrl;
import static org.example.utils.ParseSubtitle.*;
import static org.example.utils.ParseVideo.getVideoInfo;

/**
 * 字幕下载器，用于从 B 站下载视频字幕。
 */

// https://api.bilibili.com/x/web-interface/view?bvid=B
public class SubtitleDownloader {

    public static volatile List<String> subtitleList = new ArrayList<>();
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

        try (ExecutorService executor = AsyncUtil.createVirtualThreadExecutor()) {
            // 使用虚拟线程执行所有任务（关键：纯同步风格）
            for (String cid : video.cidMap.keySet()) {
                System.out.println("BV->" + bvid + "\t" + "CID->" + cid);
                String finalBvid = bvid;
                String currentCid = cid;
                String partTitle = video.cidMap.get(currentCid);

                // 直接提交同步任务（核心优化点）
                executor.execute(() -> {
                    try {
                        // 1. 同步获取字幕信息
                        String subtitleInfo = getSubtitleInfo(finalBvid, currentCid);
                        // 2. 同步下载字幕（应改为同步方法！）
                        downloadSubtitles(subtitleInfo, partTitle);
                    } catch (Exception e) {
                        // 必须抛出异常，否则任务会被标记为成功
                        throw new RuntimeException("下载失败 (CID: " + currentCid + ")", e);
                    }
                });
            }

            // 等待所有任务完成（自动传播异常）
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                throw new RuntimeException("字幕下载任务超时");
            }

            System.out.println("所有字幕下载完成");
            /*Client client = new Client();
            subtitleList.forEach(subtitle -> {
                client.addUserMessage(subtitle);
            });
            System.out.println(subtitleList);
            String str = client.send();
            System.out.println(str);
            String fileName = String.format("%s_summary.txt", Config.filePath);
            FileUtil.writeUtf8String(str, fileName);
            System.out.println("已保存AI总结字幕文件: " + fileName);*/
        } catch (Exception e) {
            // 自动捕获虚拟线程中的RuntimeException
            Throwable rootCause = (e instanceof CompletionException) ? e.getCause() : e;
            System.err.println("下载字幕时出错: " + rootCause.getMessage());
            e.printStackTrace();
        }
    }

}