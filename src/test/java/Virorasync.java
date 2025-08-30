import org.example.model.Video;
import org.example.utils.AsyncUtil;
import org.example.utils.ParseVideo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.example.config.Config.loadConfig;
import static org.example.utils.ParseSubtitle.*;
import static org.example.utils.ParseVideo.getVideoInfo;

public class Virorasync {

    @Test
    public void virtest() throws InterruptedException {//151966 ms
        loadConfig();
        System.out.println("Viror");
        long startTime = System.currentTimeMillis();

        String bvid = "BV1P94y1c7tV";
    
        Video video = ParseVideo.fromJson(getVideoInfo(bvid));
        // 计算合理的超时时间，基于任务数量，每个任务最多分配10秒
        long timeout = Math.max(100, video.cidMap.size() * 50); // 至少30秒，每个任务10秒

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
                        throw new RuntimeException("下载失败 (CID: " + currentCid + ", 标题: " + partTitle + ")", e);
                    }
                });
            }

            // 等待所有任务完成（自动传播异常）
            executor.shutdown();
            if (!executor.awaitTermination(timeout, TimeUnit.SECONDS)) {
                throw new RuntimeException("字幕下载任务超时，任务数: " + video.cidMap.size() + "，超时时间: " + timeout + "秒");
            }
        } catch (Exception e) {
            System.err.println("执行过程中发生异常: " + e.getMessage());
            throw e;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + " milliseconds");
    }

    @Test
    public void asytest() throws InterruptedException {
        loadConfig();
        System.out.println("fix");
        long startTime = System.currentTimeMillis();

        String bvid = "BV1P94y1c7tV";
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

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + " milliseconds");
    }
}
