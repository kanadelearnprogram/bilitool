package org.example;

import org.example.config.Config;
import org.example.model.Video;
import org.example.utils.ParseVideo;

import java.util.Optional;

import static org.example.config.Config.loadConfig;
import static org.example.utils.ParseBV.extractBVIdFromUrl;
import static org.example.utils.ParseSubtitle.downloadSubtitles;
import static org.example.utils.ParseSubtitle.getSubtitleInfo;
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
            for (String cid : video.cidMap.keySet()) {
                System.out.println("BV->" + bvid + "\t" + "CID->"+cid);
                String subtitleInfo = getSubtitleInfo(bvid, cid);
                downloadSubtitles(subtitleInfo,video.cidMap.get(cid));
            }
        } catch (Exception e) {
            System.err.println("下载字幕时出错: " + e.getMessage());
        }
    }

}